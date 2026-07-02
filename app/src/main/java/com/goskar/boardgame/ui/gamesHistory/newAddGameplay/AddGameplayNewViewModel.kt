package com.goskar.boardgame.ui.gamesHistory.newAddGameplay

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameplayPlayer(
    val name: String,
    val initials: String,
    val selected: Boolean = false,
)

data class SessionVariant(
    val name: String,
    val selected: Boolean = false,
)

data class AddGameplayNewState(
    val selectedGame: String = "Dune: Imperium",
    val datePlayed: String = "10/27/2023",
    val players: List<GameplayPlayer> = listOf(
        GameplayPlayer("Alex M.", "AM"),
        GameplayPlayer("Sarah K.", "SK", selected = true),
        GameplayPlayer("Jordan T.", "JT"),
    ),
    val variants: List<SessionVariant> = listOf(
        SessionVariant("Rise of Ix Expansion", selected = true),
        SessionVariant("Epic Mode"),
        SessionVariant("House Rules"),
        SessionVariant("Tournament Rules", selected = true),
        SessionVariant("Immortality Expansion"),
    ),
    val winnerIndex: Int = 1,
    val notes: String = "",
)

/**
 * Empty ViewModel that belongs to [AddGameplayNewScreen].
 * Holds only the form UI state for now — no persistence/BGG logic yet (will be wired later).
 */
class AddGameplayNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddGameplayNewState())
    val state = _state.asStateFlow()

    fun togglePlayer(index: Int) = _state.update { s ->
        s.copy(players = s.players.mapIndexed { i, p ->
            if (i == index) p.copy(selected = !p.selected) else p
        })
    }

    fun toggleVariant(index: Int) = _state.update { s ->
        s.copy(variants = s.variants.mapIndexed { i, v ->
            if (i == index) v.copy(selected = !v.selected) else v
        })
    }

    fun selectWinner(index: Int) = _state.update { it.copy(winnerIndex = index) }

    fun updateNotes(value: String) = _state.update { it.copy(notes = value) }
}
