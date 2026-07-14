package com.goskar.boardgame.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

data class RecentSession(
    val gameName: String,
    val date: String,
    val playersInitials: List<String>,
    val winner: String,
    val uri: String = "",
)

data class HomeNewState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val greeting: String = "WELCOME,",
    val totalGames: String = "0",
    val mostPlayed: String = "—",
    val winRatio: String = "0%",
    val winRatioProgress: Float = 0f,
    val recentSessions: List<RecentSession> = emptyList(),
)

class HomeNewViewModel(
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
) : ViewModel() {

    private val _state = MutableStateFlow(HomeNewState())
    val state = _state.asStateFlow()

    fun load(firstLogin: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (firstLogin) downloadFromCloud()
            computeStats()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun computeStats() {
        val games = getAllGameUseCase()
        val history = (historyRepository.getAllHistoryGame() as? RequestResult.Success)?.data ?: emptyList()
        val user = userSession.getCurrentSession()
        val uid = user?.userUID

        // Personal stats come from the Player linked to this account (picked on the Profile screen).
        val players = (playerDbRepository.getAllPlayer() as? RequestResult.Success)?.data ?: emptyList()
        val me = if (uid != null && uid != "guest") {
            players.firstOrNull { it.id == mePlayerRepository.getLinkedPlayerId(uid) }
        } else null

        val name = when {
            me != null -> me.name
            uid == "guest" -> "Guest"
            !user?.email.isNullOrBlank() -> user!!.email!!.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
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
                RecentSession(
                    gameName = h.gameName,
                    date = h.gameData.year.toString(),
                    playersInitials = h.listOfPlayer.map { initialsOf(it) },
                    winner = "Winner: ${h.winner}",
                    uri = games.find { it.baseGameId == h.baseGameId }?.uriFromBgg?:""
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
            )
        }
    }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull() }
            .take(2)
            .joinToString("")
            .ifBlank { name.take(2) }
            .uppercase()

    private fun timeGreeting(): String = when (LocalTime.now().hour) {
        in 5..11 -> "GOOD MORNING,"
        in 12..17 -> "GOOD AFTERNOON,"
        else -> "GOOD EVENING,"
    }


    private suspend fun downloadFromCloud() {
        (api.getAllGame() as? RequestResult.Success)?.let { addAllGameToDb.invoke(it.data) }
        (api.getAllPlayer() as? RequestResult.Success)?.let { addAllPlayerToDb.invoke(it.data) }
        (api.getAllHistoryGame() as? RequestResult.Success)?.let { addAllHistoryToDb.invoke(convertHistoryGameListToDto(it.data)) }
        (api.getAllHistoryGameExpansion() as? RequestResult.Success)?.let { addAllHistoryGameExpansionToDb.invoke(it.data) }
    }
}
