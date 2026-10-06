package com.goskar.boardgame.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GuestRestoreUseCase
import com.goskar.boardgame.data.useCase.MarkLocalDataSyncedUseCase
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.ui.screens.profile.viewmodel.PlayerPick
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import com.goskar.boardgame.utils.coverUri
import com.goskar.boardgame.utils.timeGreeting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecentSession(
    val gameName: String,
    val date: String,
    val playersInitials: List<String>,
    val winner: String,
    val uri: String = "",
)

data class GuestRestoreOffer(
    val updatedAt: Long,
    val gameCount: Int,
    val sessionCount: Int,
    val inProgress: Boolean = false,
    val failed: Boolean = false,
)

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val greeting: Int = R.string.home_greeting_morning,
    val totalGames: String = "0",
    val mostPlayed: String = "—",
    val winRatio: String = "0%",
    val winRatioProgress: Float = 0f,
    val recentSessions: List<RecentSession> = emptyList(),
    val needsPlayerSelection: Boolean = false,
    val availablePlayers: List<PlayerPick> = emptyList(),
    val guestRestoreOffer: GuestRestoreOffer? = null,
)

class HomeViewModel(
    private val getAllGameUseCase: GetAllGameUseCase,
    private val historyRepository: GamesHistoryDbRepository,
    private val userSession: UserRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val mePlayerRepository: MePlayerRepository,
    private val api: BoardGameFirebaseDataRepository,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase,
    private val addAllHistoryGameExpansionToDb: UpsertAllHistoryGameExpansionUseCase,
    private val guestRestore: GuestRestoreUseCase,
    private val markLocalDataSynced: MarkLocalDataSyncedUseCase,
    private val uploadToCloud: UploadToCloudUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun load(firstLogin: Boolean, mergeLocalData: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (firstLogin) {
                if (downloadFromCloud()) {
                    if (mergeLocalData) uploadToCloud() else markLocalDataSynced()
                }
                selectUser()
            }
            computeStats()
            _state.update { it.copy(isLoading = false) }
            offerGuestRestore()
        }
    }

    fun restoreGuestBackup() {
        val offer = _state.value.guestRestoreOffer ?: return
        if (offer.inProgress) return
        viewModelScope.launch {
            _state.update { it.copy(guestRestoreOffer = offer.copy(inProgress = true, failed = false)) }
            if (guestRestore.restore()) {
                _state.update { it.copy(guestRestoreOffer = null) }
                computeStats()
            } else {
                _state.update { it.copy(guestRestoreOffer = offer.copy(inProgress = false, failed = true)) }
            }
        }
    }

    fun declineGuestRestore() {
        viewModelScope.launch {
            guestRestore.decline()
            _state.update { it.copy(guestRestoreOffer = null) }
        }
    }

    private suspend fun offerGuestRestore() {
        if (_state.value.guestRestoreOffer != null) return
        if (userSession.getCurrentSession()?.userUID != "guest") return
        val meta = guestRestore.findOffer() ?: return
        _state.update {
            it.copy(
                guestRestoreOffer = GuestRestoreOffer(
                    updatedAt = meta.updatedAt,
                    gameCount = meta.gameCount,
                    sessionCount = meta.sessionCount,
                ),
            )
        }
    }

    fun selectPlayer(playerId: String) {
        viewModelScope.launch {
            val uid = userSession.getCurrentSession()?.userUID ?: return@launch
            mePlayerRepository.linkPlayer(uid, playerId)
            computeStats()
        }
    }

    private suspend fun computeStats() {
        val games = getAllGameUseCase()
        val history =
            (historyRepository.getAllHistoryGame() as? RequestResult.Success)?.data ?: emptyList()
        val user = userSession.getCurrentSession()
        val uid = user?.userUID

        val players =
            (playerDbRepository.getAllPlayer() as? RequestResult.Success)?.data ?: emptyList()
        val me = if (uid != null && uid != "guest") {
            players.firstOrNull { it.id == mePlayerRepository.getLinkedPlayerId(uid) }
        } else null

        val name = when {
            me != null -> me.name
            uid == "guest" -> "Guest"
            else -> "Player"
        }

        val mostPlayed = games.maxByOrNull { it.games }?.name ?: "—"

        // winRatio on Player is a win count; games is the play count.
        val ratio = if (me != null && me.games > 0) me.winRatio.toFloat() / me.games else 0f
        val ratioText = if (me != null && me.games > 0) "${(ratio * 100).toInt()}%" else "—"

        val recent = history
            .sortedByDescending { it.gameData }
            .take(3)
            .map { h ->
                val game = games.firstOrNull { it.name == h.gameName }

                RecentSession(
                    gameName = h.gameName,
                    date = h.gameData.year.toString(),
                    playersInitials = h.listOfPlayer.map { initialsOf(it) },
                    winner = "Winner: ${h.winner}",
                    uri = game?.coverUri() ?: ""
                )
            }

        _state.update {
            it.copy(
                userName = name,
                greeting = timeGreeting(),
                totalGames = games.size.toString(),
                mostPlayed = mostPlayed,
                winRatio = ratioText,
                winRatioProgress = ratio,
                recentSessions = recent,
                needsPlayerSelection = uid != null && uid != "guest" && me == null,
            )
        }
    }

    private suspend fun selectUser() {
        val players =
            (playerDbRepository.getAllPlayer() as? RequestResult.Success)?.data ?: emptyList()
        val user = userSession.getCurrentSession()
        val uid = user?.userUID ?: return

        val linkedId = mePlayerRepository.getLinkedPlayerId(uid)

        val me = players.firstOrNull { it.id == linkedId }

        if (me == null) {
            _state.update {
                it.copy(
                    needsPlayerSelection = true,
                    availablePlayers = players.map { p ->
                        PlayerPick(
                            p.id,
                            p.name,
                            initialsOf(p.name)
                        )
                    },
                )
            }
        }
    }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull() }
            .take(2)
            .joinToString("")
            .ifBlank { name.take(2) }
            .uppercase()


    private suspend fun downloadFromCloud(): Boolean {
        val games = api.getAllGame() as? RequestResult.Success
        val players = api.getAllPlayer() as? RequestResult.Success
        val history = api.getAllHistoryGame() as? RequestResult.Success
        val expansions = api.getAllHistoryGameExpansion() as? RequestResult.Success

        val savedGames = games?.let { addAllGameToDb.invoke(it.data) } ?: false
        val savedPlayers = players?.let { addAllPlayerToDb.invoke(it.data) } ?: false
        val savedHistory = history?.let { addAllHistoryToDb.invoke(convertHistoryGameListToDto(it.data)) } ?: false
        val savedExpansions = expansions?.let { addAllHistoryGameExpansionToDb.invoke(it.data) } ?: false
        return savedGames && savedPlayers && savedHistory && savedExpansions
    }
}
