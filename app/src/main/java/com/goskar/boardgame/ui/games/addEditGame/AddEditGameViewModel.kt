package com.goskar.boardgame.ui.games.addEditGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AddEditGameState(
    val name: String? = null,
    val expansion: Boolean = false,
    val baseGame: String? = null,
    val minPlayer: String = "",
    val maxPlayer: String = "",
    val games: Int = 0,
    val id: String? = null ,

    val successAddEditGame: Boolean = false,
    val errorVisible: Boolean = false
)

class AddEditGameViewModel(
    private val gameNetworkRepository: GameNetworkRepository
) : ViewModel(){

    private val _state = MutableStateFlow(AddEditGameState())
    val state = _state.asStateFlow()

    fun update(state: AddEditGameState) {
        _state.update { state }
    }

    fun validateAddGame() {
        viewModelScope.launch {
            val game = Game(
                name = state.value.name?:"",
                expansion = state.value.expansion,
                baseGame = state.value.baseGame?:"",
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                id = UUID.randomUUID().toString()
                )
            val response = gameNetworkRepository.addGame(game)

            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true
                        )
                    }
                }
                else -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = false,
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }

    fun validateEditGame() {
        viewModelScope.launch {
            val game = Game(
                name = state.value.name?:"",
                expansion = state.value.expansion,
                baseGame = state.value.baseGame?:"",
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                id = state.value.id?:""
            )
            val response = gameNetworkRepository.editGame(game)

            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true
                        )
                    }
                }
                else -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = false,
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }


}