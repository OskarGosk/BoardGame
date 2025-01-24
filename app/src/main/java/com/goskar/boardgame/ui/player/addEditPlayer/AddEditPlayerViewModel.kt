package com.goskar.boardgame.ui.player.addEditPlayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.oflineRepository.PlayerDbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class AddEditPLayerState(
    val name: String = "",
//    val image: Image,
    val games: Int = 0,
    val winRatio: Int = 0,
    val description: String = "",
    var selected: Boolean = false,
    val id: String = "",

    val successAddEditPlayer: Boolean = false,
    val errorVisible: Boolean = false
)

@KoinViewModel
class AddEditPlayerViewModel(
    private val playerNetworkRepository: PlayerNetworkRepository,
    private val playerDbRepository: PlayerDbRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditPLayerState())
    val state = _state.asStateFlow()

    fun update(state: AddEditPLayerState) {
        _state.update { state }
    }

    fun validateAddPLayer() {
        viewModelScope.launch {
            val player = Player(
                name = state.value.name,
                games = state.value.games,
                winRatio = state.value.winRatio,
                description = state.value.description,
                selected = state.value.selected,
                id = state.value.id
            )
            playerDbRepository.insertPlayer(player)
            val response = playerNetworkRepository.addPlayer(player)

            when (response){
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddEditPlayer = true
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

    fun validateEditPlayer() {
        viewModelScope.launch {
            val player = Player(
                name = state.value.name,
                games = state.value.games,
                winRatio = state.value.winRatio,
                description = state.value.description,
                selected = state.value.selected,
                id = state.value.id
            )
            playerDbRepository.editPlayer(player)

//            when (response){
//                is RequestResult.Success -> {
//                    _state.update {
//                        it.copy(
//                            successAddEditPlayer = true
//                        )
//                    }
//                }
//                else -> {
//                    _state.update {
//                        it.copy(
//                            errorVisible = true
//                        )
//                    }
//                }
//            }
        }
    }
}
