package com.goskar.boardgame.ui.gamesHistory

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.theme.AppChipStyle
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

data class HistoryGroup(val title: String, val sessions: List<HistorySession>)

data class GamesHistoryState(
    // Raw data
    val historyList: List<HistoryGame> = emptyList(),
    val historyGameWithExpansion: List<HistoryGameWithExpansion> = emptyList(),
    val games: List<Game> = emptyList(),
    val query: String = "",
    val sortOption: Int = R.string.default_sort,
    val loading: Boolean = true,
    
    // UI derived data
    val filters: List<String> = listOf("All Games", "Wins Only", "Strategy", "Co-op"),
    val selectedFilter: Int = 0,
    val showingLabel: String = "",
    val groups: List<HistoryGroup> = emptyList(),
)

class GamesHistoryViewModel(
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getHistoryWithExpansionUseCase: GetHistoryWithExpansionUseCase,
    private val getAllGameUseCase: GetAllGameUseCase
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
    }

    private fun loadGames() {
        viewModelScope.launch {
            _state.update { it.copy(games = getAllGameUseCase()) }
        }
    }

    fun updateSearchTxt(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun updateSortOption(value: Int) {
        _state.update { it.copy(sortOption = value) }
    }

    fun updateSelectedFilter(index: Int) {
        _state.update { it.copy(selectedFilter = index) }
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
        val q = query.trim()
        val filtered = if (q.isBlank()) historyList
        else historyList.filter { it.gameName.contains(q, true) || it.winner.contains(q, true) }
        
        val sorted = filtered.sortedByDescending { it.gameData }
        val categoryByName = games.associate { it.name to it.category }
        val uriByName = games.associate { it.name to (it.uri ?: it.uriFromBgg) }
        val today = LocalDate.now()
        
        val byBucket = sorted.groupBy { bucketLabel(it.gameData, today) }
        val groups = listOf("Today", "Yesterday", "This Week", "Earlier").mapNotNull { label ->
            byBucket[label]?.let { HistoryGroup(label, it.map { g -> g.toSession(categoryByName[g.gameName], uriByName[g.gameName]) }) }
        }

        return this.copy(
            showingLabel = "Showing ${filtered.size} session${if (filtered.size != 1) "s" else ""}",
            groups = groups
        )
    }

    private fun bucketLabel(date: LocalDate, today: LocalDate): String = when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        date.isAfter(today.minusDays(7)) -> "This Week"
        else -> "Earlier"
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
