package com.goskar.boardgame.ui.player.playerList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Player
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
    private val playerNetworkRepository: PlayerNetworkRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerListState())
    val state = _state.asStateFlow()

    fun update(state: PlayerListState) {
        _state.update { state }
    }

    fun getAllPlayer() {
        viewModelScope.launch {
            val response = playerNetworkRepository.getAllPlayer()?.toMutableList()
            _state.update {
                it.copy(
                    playerList = response
                )
            }
        }
    }

    fun validateDeletePlayer(playerID: String) {
        viewModelScope.launch {
            val response = playerNetworkRepository.deletePlayer(playerId = playerID)
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successDeletePlayer = true
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