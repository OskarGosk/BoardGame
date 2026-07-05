package com.goskar.boardgame.ui.screens.addPlayer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class AddPlayerNewViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddPlayerNewState())
    val state = _state.asStateFlow()

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
