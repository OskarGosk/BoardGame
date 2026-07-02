package com.goskar.boardgame.ui.gamesHistory.newHistory

import androidx.lifecycle.ViewModel
import com.goskar.boardgame.ui.theme.BgDarkChipStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HistorySession(
    val gameName: String,
    val category: String,
    val categoryStyle: BgDarkChipStyle,
    val durationMin: Int,
    val avatars: List<String>,
    val extraPlayers: Int,
    val winner: String,
    val winnerIsYou: Boolean = false,
)

data class HistoryGroup(
    val title: String,
    val sessions: List<HistorySession>,
)

data class HistoryNewState(
    val query: String = "",
    val filters: List<String> = listOf("All Games", "Wins Only", "Strategy", "Co-op"),
    val selectedFilter: Int = 0,
    val showingLabel: String = "Showing 4 of 248 sessions",
    val groups: List<HistoryGroup> = listOf(
        HistoryGroup(
            title = "Today",
            sessions = listOf(
                HistorySession(
                    gameName = "Dune: Imperium",
                    category = "Strategy",
                    categoryStyle = BgDarkChipStyle.CATEGORY,
                    durationMin = 114,
                    avatars = listOf("JD", "AL"),
                    extraPlayers = 2,
                    winner = "M. Kane",
                ),
            ),
        ),
        HistoryGroup(
            title = "Yesterday",
            sessions = listOf(
                HistorySession(
                    gameName = "Terraforming Mars",
                    category = "Euro",
                    categoryStyle = BgDarkChipStyle.EXPANSION,
                    durationMin = 185,
                    avatars = listOf("SK", "EL"),
                    extraPlayers = 0,
                    winner = "S. Kim",
                ),
            ),
        ),
        HistoryGroup(
            title = "Last Week",
            sessions = listOf(
                HistorySession(
                    gameName = "Azul",
                    category = "Abstract",
                    categoryStyle = BgDarkChipStyle.STATUS_WIN,
                    durationMin = 45,
                    avatars = listOf("ME", "YJ"),
                    extraPlayers = 0,
                    winner = "You (ME)",
                    winnerIsYou = true,
                ),
                HistorySession(
                    gameName = "Clank! Catacombs",
                    category = "Deck Builder",
                    categoryStyle = BgDarkChipStyle.BASE_GAME,
                    durationMin = 82,
                    avatars = listOf("JD", "AL"),
                    extraPlayers = 0,
                    winner = "J. Doe",
                ),
            ),
        ),
    ),
)

/**
 * Empty ViewModel that belongs to [HistoryNewScreen].
 * Holds only placeholder history state for now — no data source yet (will be wired later).
 */
class HistoryNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(HistoryNewState())
    val state = _state.asStateFlow()

    fun updateQuery(value: String) = _state.update { it.copy(query = value) }

    fun selectFilter(index: Int) = _state.update { it.copy(selectedFilter = index) }
}
