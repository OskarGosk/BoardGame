package com.goskar.boardgame.ui.gamesList.addEditGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import kotlinx.coroutines.delay
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
    val uri: String? = null,
    val id: String? = null,

    val successAddEditGame: Boolean = false,
    val errorVisible: Boolean = false,
    val inProgress: Boolean = false
)

class AddEditGameViewModel(
    private val gameDbRepository: GameDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditGameState())
    val state = _state.asStateFlow()

    fun update(state: AddEditGameState) {
        _state.update { state }
    }

    fun validateAddGame() {
        _state.update {
            it.copy(
                inProgress = true
            )
        }
        viewModelScope.launch {
            delay(1000)
            val game = Game(
                name = state.value.name ?: "",
                expansion = state.value.expansion,
                baseGame = state.value.baseGame ?: "",
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                uri = state.value.uri,
                id = UUID.randomUUID().toString()
            )
            val response = gameDbRepository.insertGame(game)

            when (response) {
                true -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true,
                            inProgress = false
                        )
                    }
                }

                false -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = false,
                            errorVisible = true,
                            inProgress = false
                        )
                    }
                }
            }
        }
    }

    fun validateEditGame() {
        viewModelScope.launch {
            val game = Game(
                name = state.value.name ?: "",
                expansion = state.value.expansion,
                baseGame = state.value.baseGame ?: "",
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                uri = state.value.uri,
                id = state.value.id ?: ""
            )
            val response = gameDbRepository.editGame(game)

            when (response) {
                true -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true
                        )
                    }
                }

                false -> {
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