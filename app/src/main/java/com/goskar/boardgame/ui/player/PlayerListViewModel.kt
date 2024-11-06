package com.goskar.boardgame.ui.player

import OGosk.boardgamebase.model.OperationStatus
import OGosk.boardgamebase.model.Player
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class PlayerListState(
    val playerList: List<Player> = emptyList()
)

@KoinViewModel
class PlayerListViewModel(
    private val playerNetworkRepository: PlayerNetworkRepository
) : ViewModel() {

    companion object {
        const val TAG = "Player List"
    }

    init {
        getAllPlayer()
    }

    private val _state = MutableStateFlow(PlayerListState())
    val state = _state.asStateFlow()

    private fun getAllPlayer () {
        viewModelScope.launch {
            val response = playerNetworkRepository.getAllPlayer().toMutableList()
            _state.update {
                it.copy(
                    playerList = response
                )
            }
        }
    }

}