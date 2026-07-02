package com.goskar.boardgame.ui.gamesHistory.newSessionDetails

import androidx.lifecycle.ViewModel
import com.goskar.boardgame.ui.theme.BgDarkChipStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionPlayerResult(
    val name: String,
    val initials: String,
    val score: String,
    val isWinner: Boolean = false,
)

data class SessionDetailsNewState(
    val gameName: String = "Dune: Imperium",
    val category: String = "Strategy",
    val categoryStyle: BgDarkChipStyle = BgDarkChipStyle.CATEGORY,
    val dateLabel: String = "Oct 27, 2023",
    val duration: String = "114m",
    val playerCount: String = "4",
    val players: List<SessionPlayerResult> = listOf(
        SessionPlayerResult("M. Kane", "MK", "84 pts", isWinner = true),
        SessionPlayerResult("Alex M.", "AM", "72 pts"),
        SessionPlayerResult("Sarah K.", "SK", "65 pts"),
        SessionPlayerResult("Jordan T.", "JT", "58 pts"),
    ),
    val variants: List<String> = listOf("Rise of Ix Expansion", "Tournament Rules"),
    val notes: String = "Tight game that came down to the final round — M. Kane secured the win with a last-turn Imperium play.",
)

/**
 * Empty ViewModel that belongs to [SessionDetailsNewScreen].
 * Read-only presentation of a logged session — holds placeholder state for now.
 */
class SessionDetailsNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(SessionDetailsNewState())
    val state = _state.asStateFlow()
}
