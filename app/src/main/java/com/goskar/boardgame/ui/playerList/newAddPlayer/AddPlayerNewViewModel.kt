package com.goskar.boardgame.ui.playerList.newAddPlayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Data needed to open [AddPlayerNewScreen] in edit mode (prefilled).
 */
data class EditPlayerData(
    val name: String,
    val avatarIndex: Int = 0,
    val skillIndex: Int = 0,
)

data class AddPlayerNewState(
    val avatars: List<String> = listOf("KN", "RG", "MG"),
    val selectedAvatar: Int = 0,
    val nickname: String = "",
    val selectedSkill: Int = 0,
    val isEditMode: Boolean = false,
)

/**
 * Empty ViewModel that belongs to [AddPlayerNewScreen].
 * Holds only the form UI state for now — no persistence logic yet (will be wired later).
 * Serves both "add" and "edit" — [loadForEdit] prefills the form and flips [AddPlayerNewState.isEditMode].
 */
class AddPlayerNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddPlayerNewState())
    val state = _state.asStateFlow()

    /** Prefill the form from an existing player. Called once when the screen opens in edit mode. */
    fun loadForEdit(data: EditPlayerData) = _state.update {
        it.copy(
            nickname = data.name,
            selectedAvatar = data.avatarIndex.coerceIn(0, it.avatars.lastIndex),
            selectedSkill = data.skillIndex,
            isEditMode = true,
        )
    }

    fun selectAvatar(index: Int) = _state.update { it.copy(selectedAvatar = index) }

    fun updateNickname(value: String) = _state.update { it.copy(nickname = value) }

    fun selectSkill(index: Int) = _state.update { it.copy(selectedSkill = index) }
}
