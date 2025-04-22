package com.goskar.boardgame.ui.gameDetailsBGG

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class GameDetailsBGGState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val successAddEditGame: Boolean = false,
    val gameName: String? = "",
    val cooperate: Boolean = false,
    val expansion: Boolean = false,
    val baseGame: String? = null
)

class GameDetailsBGGViewModel(
    private val boardGameApiRepository: BoardGameApiRepository,
    private val gameDbRepository: GameDbRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsBGGState())
    val state = _state.asStateFlow()

    private val _gameDetails = MutableStateFlow<BoardGamesDetails?>(null)
    val gameDetails = _gameDetails.asStateFlow()

    fun update(state: GameDetailsBGGState) {
        _state.update { state }
    }

    fun getGame(gameID: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            when (val response = boardGameApiRepository.getGame(gameID)) {
                is RequestResult.Success -> {
                    _gameDetails.value = response.data
                    Log.d("Oskar22","$response")
                }

                else -> {
                    _state.update {
                        it.copy(
                            isError = true
                        )
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun validateAddGame() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val gameToAdd = gameDetails.value?.boardGamesBGG?.first()
            val game = Game(
                name = state.value.gameName ?: "",
                expansion = state.value.expansion,
                cooperate = state.value.cooperate,
                baseGame = state.value.baseGame ?: "",
                minPlayer = gameToAdd?.minPlayers.toString(),
                maxPlayer = gameToAdd?.maxPlayers.toString(),
                games = 0,
                uriFromBgg = gameToAdd?.image,
                id = UUID.randomUUID().toString()
            )

            val response = gameDbRepository.insertGame(game)

            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true,
                            isLoading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = false,
                            isError = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }



}