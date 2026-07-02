package com.goskar.boardgame.ui.playerList.newPlayerList

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DirectoryPlayer(
    val name: String,
    val role: String,
    val initials: String,
    val winRate: Double,
    val rank: String,
    val online: Boolean,
)

data class PlayerListNewState(
    val query: String = "",
    val totalPlayers: String = "1,284",
    val avgWinRate: String = "48.2%",
    val activeThisWeek: String = "412",
    val players: List<DirectoryPlayer> = listOf(
        DirectoryPlayer("Alex \"MeepleKing\" Chen", "Grand Strategist · Joined Mar 2023", "AC", 74.2, "#12", online = true),
        DirectoryPlayer("Sarah J. Miller", "Tactical Advisor · Joined Jan 2024", "SM", 52.8, "#156", online = false),
        DirectoryPlayer("Marcus \"CritRoll\" Thorne", "Dungeon Master · Joined Oct 2023", "MT", 61.5, "#45", online = true),
        DirectoryPlayer("Elena Rodriguez", "Resource Expert · Joined Dec 2023", "ER", 39.1, "#1,102", online = false),
    ),
)

/**
 * Empty ViewModel that belongs to [PlayerListNewScreen].
 * Holds only placeholder directory state for now — no data source yet (will be wired later).
 */
class PlayerListNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(PlayerListNewState())
    val state = _state.asStateFlow()

    fun updateQuery(value: String) = _state.update { it.copy(query = value) }
}
