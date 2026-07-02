package com.goskar.boardgame.ui.home.newHome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder session row for the new Home screen.
 * Logic (real data source) will be wired in later — for now the screen shows dummy content.
 */
data class RecentSession(
    val gameName: String,
    val date: String,
    val playersInitials: List<String>,
    val winner: String,
)

data class HomeNewState(
    val userName: String = "Alex",
    val greeting: String = "GOOD MORNING,",
    val totalGames: String = "142",
    val mostPlayed: String = "Terraforming Mars",
    val winRatio: String = "64%",
    val winRatioProgress: Float = 0.64f,
    val recentSessions: List<RecentSession> = listOf(
        RecentSession(
            gameName = "Wingspan",
            date = "2019",
            playersInitials = listOf("AL", "SK", "JD"),
            winner = "Winner: Alex (84 pts)",
        ),
        RecentSession(
            gameName = "Root",
            date = "2018",
            playersInitials = listOf("AL", "MK", "GR"),
            winner = "Winner: Alex (72 pts)",
        ),
        RecentSession(
            gameName = "Everdell",
            date = "2018",
            playersInitials = listOf("AL", "SK"),
            winner = "Winner: Alex (91 pts)",
        ),
    ),
)

/**
 * Empty ViewModel that belongs to [HomeNewScreen].
 * Currently only holds placeholder state — no business logic yet.
 */
class HomeNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeNewState())
    val state = _state.asStateFlow()
}
