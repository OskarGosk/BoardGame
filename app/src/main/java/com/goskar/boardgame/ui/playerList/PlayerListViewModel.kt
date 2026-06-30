package com.goskar.boardgame.ui.playerList

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface PlayerListEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : PlayerListEvent
}

data class PlayerListState(
    val playerList: List<Player>? = mutableListOf(),
    val visibleDialog: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val player: Player? = null,
    val isLoading: Boolean = false,
    val showAddEditDialog: Boolean = false
)

class PlayerListViewModel(
    private val playerDbRepository: PlayerDbRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(PlayerListState())
    val state = _state.asStateFlow()

    private val _events = Channel<PlayerListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateSearchTxt(value: String) {
        _state.update { it.copy(searchTxt = value) }
    }

    fun updateSortOption(value: Int) {
        _state.update { it.copy(sortOption = value) }
    }

    fun updateShowAddEditDialog(value: Boolean) {
        _state.update { it.copy(showAddEditDialog = value) }
    }

    fun updatePlayer(value :Player) {
        _state.update { it.copy(player = value) }
    }



    fun getAllPlayer() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val response = playerDbRepository.getAllPlayer()
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            playerList = response.data,
                            isLoading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }

        }
    }

    fun validateDeletePlayer(player: Player) {
        viewModelScope.launch {
            val response = playerDbRepository.deletePlayer(player = player)
            when (response) {
                is RequestResult.Success -> {
                    _events.send(PlayerListEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS))
                    getAllPlayer()
                }

                is RequestResult.Error -> {
                    _events.send(PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                }
            }
        }
    }

    fun validateAddEditPLayer(newPlayer: Boolean) {
        viewModelScope.launch {
            val response = state.value.player?.let {
                if (newPlayer) playerDbRepository.insertPlayer(it) else playerDbRepository.editPlayer(
                    it
                )
            }

            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            player = null,
                        )
                    }
                    getAllPlayer()
                }

                is RequestResult.Error -> {
                    _events.send(PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                }
                else -> {
                    _events.send(PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                }
            }
        }
    }
}