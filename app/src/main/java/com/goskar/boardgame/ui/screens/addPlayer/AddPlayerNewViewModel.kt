package com.goskar.boardgame.ui.screens.addPlayer

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.screens.players.PlayerListEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class EditPlayerData(
    val id: String,
    val name: String,
    val avatarIndex: Int = 0,
    val skillIndex: Int = 0,
    val games: Int = 0,
    val winRatio: Int = 0,
    val description: String = "",
    val selected: Boolean = false,
)

data class AddPlayerNewState(
    val avatars: List<String> = listOf("KN", "RG", "MG"),
    val selectedAvatar: Int = 0,
    val nickname: String = "",
    val selectedSkill: Int = 0,
    val isEditMode: Boolean = false,
    val editPlayerId: String? = null,
    val games: Int = 0,
    val winRatio: Int = 0,
    val description: String = "",
    val selected: Boolean = false,
)

sealed interface PlayerEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : PlayerEvent
    data class SuccessAddEditPlayer(@StringRes val message: Int, val type: AppSnackBarType) : PlayerEvent
}

class AddPlayerNewViewModel(
    private val playerDbRepository: PlayerDbRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(AddPlayerNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<PlayerEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loadForEdit(data: EditPlayerData) = _state.update {
        it.copy(
            nickname = data.name,
            selectedAvatar = data.avatarIndex.coerceIn(0, it.avatars.lastIndex),
            selectedSkill = data.skillIndex,
            isEditMode = true,
            editPlayerId = data.id,
            games = data.games,
            winRatio = data.winRatio,
            description = data.description,
            selected = data.selected
        )
    }

    fun selectAvatar(index: Int) = _state.update { it.copy(selectedAvatar = index) }

    fun updateNickname(value: String) = _state.update { it.copy(nickname = value) }

    fun selectSkill(index: Int) = _state.update { it.copy(selectedSkill = index) }

    fun validateAddEditPLayer() {
        viewModelScope.launch {
            val currentState = state.value
            val player = Player(
                id = currentState.editPlayerId ?: UUID.randomUUID().toString(),
                name = currentState.nickname,
                games = currentState.games,
                winRatio = currentState.winRatio,
                description = currentState.description,
                selected = currentState.selected,
                selectedSkill = currentState.selectedSkill
            )

            val response = if (currentState.isEditMode) {
                playerDbRepository.editPlayer(player)
            } else {
                playerDbRepository.insertPlayer(player)
            }

            when (response) {
                is RequestResult.Success -> {
                    _events.send(PlayerEvent.SuccessAddEditPlayer(R.string.player_added_successfully, AppSnackBarType.SUCCESS))
                }

                is RequestResult.Error -> {
                    _events.send(PlayerEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                }
            }
        }
    }
}
