package com.goskar.boardgame.ui.profile.newProfile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MedalItem(
    val title: String,
    val description: String,
)

data class ProfileNewState(
    val name: String = "Julian Thorne",
    val subtitle: String = "Dungeon Master Level 42",
    val initials: String = "JT",
    val gamesLogged: String = "148",
    val winRate: String = "64%",
    val notificationsActive: Boolean = true,
    val lastSynced: String = "Last synced: 2m ago",
    val medals: List<MedalItem> = listOf(
        MedalItem("Tactical Genius", "Won 5 games of Scythe"),
        MedalItem("Social Butterfly", "Played with 20 unique players"),
    ),
)

/**
 * Empty ViewModel that belongs to [ProfileNewScreen].
 * Holds only placeholder profile state for now — no data source/actions wired yet.
 */
class ProfileNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileNewState())
    val state = _state.asStateFlow()
}
