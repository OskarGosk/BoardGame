package com.goskar.boardgame.ui.gamesList.newAddGame

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PopularMatch(
    val name: String,
    val category: String,
    val year: String,
)

data class AddGameNewState(
    val query: String = "",
    val selectedTab: Int = 0, // 0 = BGG Search, 1 = Manual Entry
    val popularMatches: List<PopularMatch> = listOf(
        PopularMatch("Everdell", "Strategy", "2018"),
        PopularMatch("Wingspan", "Family", "2019"),
        PopularMatch("Gloomhaven", "Thematic", "2017"),
    ),
    // --- Manual Entry form ---
    val name: String = "",
    val minPlayers: String = "",
    val maxPlayers: String = "",
    val cooperate: Boolean = false,
    val expansion: Boolean = false,
    val baseGame: String = "",
    val baseGameOptions: List<String> = listOf("Wingspan", "Terraforming Mars", "Root"),
    val hasCover: Boolean = false,
)

/**
 * Empty ViewModel that belongs to [AddGameNewScreen].
 * Holds only the form UI state for now — no BGG search logic yet (will be wired later).
 */
class AddGameNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddGameNewState())
    val state = _state.asStateFlow()

    fun updateQuery(value: String) = _state.update { it.copy(query = value) }

    fun selectTab(index: Int) = _state.update { it.copy(selectedTab = index) }

    // --- Manual Entry form ---
    fun updateName(value: String) = _state.update { it.copy(name = value) }

    fun updateMinPlayers(value: String) =
        _state.update { it.copy(minPlayers = value.filter { c -> c.isDigit() }) }

    fun updateMaxPlayers(value: String) =
        _state.update { it.copy(maxPlayers = value.filter { c -> c.isDigit() }) }

    fun toggleCooperate(value: Boolean) = _state.update { it.copy(cooperate = value) }

    fun toggleExpansion(value: Boolean) = _state.update {
        // clearing the base game when it is no longer an expansion keeps state consistent
        it.copy(expansion = value, baseGame = if (value) it.baseGame else "")
    }

    fun selectBaseGame(value: String) = _state.update { it.copy(baseGame = value) }
}
