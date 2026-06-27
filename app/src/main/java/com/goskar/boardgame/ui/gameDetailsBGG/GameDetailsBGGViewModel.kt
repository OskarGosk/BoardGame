package com.goskar.boardgame.ui.gameDetailsBGG

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.bbg.BoardGameApiRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface GameDetailsEvent {
    data class SuccessAddEditGame(val message: Int, val type: AppSnackBarType) : GameDetailsEvent
}

data class GameDetailsBGGState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val gameName: String? = "",
    val gameId: String = "",
    val cooperate: Boolean = false,
    val expansion: Boolean = false,
    val baseGame: String? = null,
    val baseGameId: String? = null,
    val selectedBaseGame: String? = null
)

class GameDetailsBGGViewModel(
    private val boardGameApiRepository: BoardGameApiRepository,
    private val gameDbRepository: GameDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsBGGState())
    val state = _state.asStateFlow()

    private val _events = Channel<GameDetailsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _gameDetails = MutableStateFlow<BoardGamesDetails?>(null)
    val gameDetails = _gameDetails.asStateFlow()

    private val _allBaseGame = MutableStateFlow<List<Game>>(emptyList())
    val allBaseGame = _allBaseGame.asStateFlow()

    init {
        viewModelScope.launch {
            _allBaseGame.value = getAllGameUseCase.invoke().filter { !it.expansion }
        }
    }

    fun update(state: GameDetailsBGGState) {
        _state.update { state }
    }

    fun getGame() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            when (val response = boardGameApiRepository.getGame(_state.value.gameId)) {
                is RequestResult.Success -> {
                    _gameDetails.value = response.data
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
                baseGameId = state.value.baseGameId,
                minPlayer = gameToAdd?.minPlayers?.toString() ?: "0",
                maxPlayer = gameToAdd?.maxPlayers?.toString() ?: "0",
                games = 0,
                uriFromBgg = gameToAdd?.image,
                id = UUID.randomUUID().toString()
            )

            val response = gameDbRepository.insertGame(game)

            when (response) {
                is RequestResult.Success -> {
                    _events.send(GameDetailsEvent.SuccessAddEditGame(R.string.success_global, AppSnackBarType.SUCCESS))
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            isError = true,
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
}