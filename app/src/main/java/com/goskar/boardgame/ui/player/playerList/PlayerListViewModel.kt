package com.goskar.boardgame.ui.player.playerList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.oflineRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class PlayerListState(
    val playerList: List<Player>? = mutableListOf(),
    val successDeletePlayer: Boolean = false,
    val errorVisible: Boolean = false,
    val visibleDialog: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort
)

@KoinViewModel
class PlayerListViewModel(
    private val playerDbRepository: PlayerDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerListState())
    val state = _state.asStateFlow()

    fun update(state: PlayerListState) {
        _state.update { state }
    }

    fun getAllPlayer() {
        viewModelScope.launch {
            val response = playerDbRepository.getAllPlayer()
            when (response) {
                is RequestResult.SuccessWithData -> {
                    _state.update {
                        it.copy(
                            playerList = response.data as List<Player>,
                            errorVisible = false
                        )
                    }
                }
                else -> {
                    _state.update {
                        it.copy(
                            errorVisible = true
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
                    _state.update {
                        it.copy(
                            successDeletePlayer = true,
                            errorVisible = false
                        )
                    }
                    getAllPlayer()
                }

                else -> {
                    _state.update {
                        it.copy(
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }
}