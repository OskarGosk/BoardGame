package com.goskar.boardgame.ui.screens.history.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.isWinner
import com.goskar.boardgame.data.models.playMode
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface GameHistoryEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GameHistoryEvent
}

data class HistorySession(
    val id: String,
    val gameName: String,
    val durationMin: Int,
    val avatars: List<String>,
    val extraPlayers: Int,
    val winner: String,
    val winnerIsYou: Boolean = false,
    val uri: String
)

/** Filtry listy sesji; etykieta jest zasobem tekstowym. */
enum class HistoryFilter(@StringRes val label: Int) {
    ALL(R.string.history_filter_all),
    WINS_ONLY(R.string.history_filter_wins),
    COOP(R.string.history_filter_coop),
    TEAMS(R.string.history_filter_teams),
}

/** Przedział czasu, w który grupowane są sesje. */
enum class HistoryBucket(@StringRes val label: Int) {
    TODAY(R.string.history_bucket_today),
    YESTERDAY(R.string.history_bucket_yesterday),
    THIS_WEEK(R.string.history_bucket_this_week),
    EARLIER(R.string.history_bucket_earlier),
}

data class HistoryGroup(val bucket: HistoryBucket, val sessions: List<HistorySession>)

data class GamesHistoryState(
    // Raw data
    val historyList: List<HistoryGame> = emptyList(),
    val historyGameWithExpansion: List<HistoryGameWithExpansion> = emptyList(),
    val games: List<Game> = emptyList(),
    val query: String = "",
    val sortOption: Int = R.string.default_sort,
    val loading: Boolean = true,
    val mePlayerName: String? = null,
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,

    // UI derived data
    val filters: List<HistoryFilter> = HistoryFilter.entries,
    val showingCount: Int = 0,
    val groups: List<HistoryGroup> = emptyList(),
)

class GamesHistoryViewModel(
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getHistoryWithExpansionUseCase: GetHistoryWithExpansionUseCase,
    private val getAllGameUseCase: GetAllGameUseCase,
    private val userSession: UserRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val mePlayerRepository: MePlayerRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GamesHistoryState())
    
    // Single source of truth for the UI
    val state = _state.map { it.calculateDerivedFields() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamesHistoryState())

    private val _events = Channel<GameHistoryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        getAllHistoryGame()
        validateGetHistoryGameWithExpansion()
        loadGames()
        loadMePlayer()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _state.update { it.copy(games = getAllGameUseCase()) }
        }
    }

    private fun loadMePlayer() {
        viewModelScope.launch {
            val uid = userSession.getCurrentSession()?.userUID
            if (uid == null || uid == "guest") return@launch
            val linkedId = mePlayerRepository.getLinkedPlayerId(uid) ?: return@launch
            val me = (playerDbRepository.getAllPlayer() as? RequestResult.Success)
                ?.data?.firstOrNull { it.id == linkedId }
            _state.update { it.copy(mePlayerName = me?.name) }
        }
    }

    fun updateSearchTxt(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun updateSortOption(value: Int) {
        _state.update { it.copy(sortOption = value) }
    }

    fun updateSelectedFilter(filter: HistoryFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    fun getAllHistoryGame() {
        viewModelScope.launch {
            val response = gamesHistoryDbRepository.getAllHistoryGame()
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            historyList = response.data.sortedBy { it.gameData },
                            loading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    private fun validateGetHistoryGameWithExpansion() {
        viewModelScope.launch {
            when (val response = getHistoryWithExpansionUseCase.invoke()) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            historyGameWithExpansion = response.data.sortedBy { it.history.gameData },
                            loading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    private fun GamesHistoryState.calculateDerivedFields(): GamesHistoryState {
        val me = mePlayerName
        val availableFilters = HistoryFilter.entries.filter { it != HistoryFilter.WINS_ONLY || me != null }
        val activeFilter = selectedFilter.takeIf { it in availableFilters } ?: HistoryFilter.ALL
        val q = query.trim()
        val filtered = historyList
            .filter { if (q.isBlank()) true else it.gameName.contains(q, true) || it.winner.contains(q, true) }
            .filter { session ->
                when (activeFilter) {
                    HistoryFilter.ALL -> true
                    // A co-op win counts for everyone in the session, so participation has to be checked too.
                    HistoryFilter.WINS_ONLY -> me != null &&
                        session.listOfPlayer.any { it.equals(me, ignoreCase = true) } && session.isWinner(me)
                    HistoryFilter.COOP -> session.playMode() == GameMode.COOPERATIVE
                    HistoryFilter.TEAMS -> session.playMode() == GameMode.TEAM
                }
            }

        val sorted = filtered.sortedByDescending { it.gameData }
        val categoryByName = games.associate { it.name to it.category }
        val uriByName = games.associate { it.name to (it.uri ?: it.uriFromBgg) }
        val today = LocalDate.now()
        
        val byBucket = sorted.groupBy { bucketOf(it.gameData, today) }
        val groups = HistoryBucket.entries.mapNotNull { bucket ->
            byBucket[bucket]?.let { HistoryGroup(bucket, it.map { g -> g.toSession(categoryByName[g.gameName], uriByName[g.gameName]) }) }
        }

        return this.copy(
            filters = availableFilters,
            selectedFilter = activeFilter,
            showingCount = filtered.size,
            groups = groups
        )
    }

    private fun bucketOf(date: LocalDate, today: LocalDate): HistoryBucket = when {
        date == today -> HistoryBucket.TODAY
        date == today.minusDays(1) -> HistoryBucket.YESTERDAY
        date.isAfter(today.minusDays(7)) -> HistoryBucket.THIS_WEEK
        else -> HistoryBucket.EARLIER
    }

    private fun HistoryGame.toSession(category: String?, gameUri: String?): HistorySession = HistorySession(
        id = id,
        gameName = gameName,
        durationMin = durationMin ?: 0,
        avatars = listOfPlayer.map { historyInitials(it) }.take(3),
        extraPlayers = (listOfPlayer.size - 3).coerceAtLeast(0),
        winner = winner,
        uri = gameUri ?: ""
    )

    private fun historyInitials(name: String): String =
        name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
            .joinToString("").ifBlank { name.take(2) }.uppercase()
}
