package com.goskar.boardgame.ui.gamesList.lists

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.utils.SortList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameListEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GameListEvent
}
data class GameListState(
    val gameList: List<GameUiState>? = emptyList(),
    val gameListEdited: List<GameUiState> = emptyList(),
    val searchTxt: String = "",
    val sortOption: SortList = SortList.DEFAULT,
    val isLoading: Boolean = false,
    val checkboxBaseGame: Boolean = true,
    val checkboxExpansionGame: Boolean = true,
    val checkboxAllGame: Boolean = true
)

data class GameUiState(
    val game: Game,
    val isExpanded: Boolean = true
)

class GameListViewModel(
    private val gameDbRepository: GameDbRepository
) : ViewModel() {


    private val _state = MutableStateFlow(GameListState())
    val state = _state.asStateFlow()

    private val _events = Channel<GameListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    fun update(state: GameListState) {
        _state.update { state }
    }

    fun refreshGameList() {
        val newGameList: List<GameUiState> = when (state.value.sortOption) {
            SortList.DEFAULT -> state.value.gameList ?: emptyList()
            SortList.A_Z -> state.value.gameList?.sortedBy { it.game.name }
                ?: emptyList()

            SortList.Z_A -> state.value.gameList?.sortedByDescending { it.game.name }
                ?: emptyList()

            SortList.GAMES_MIN -> state.value.gameList?.sortedBy { it.game.games }
                ?: emptyList()

            SortList.GAMES_MAX -> state.value.gameList?.sortedByDescending { it.game.games }
                ?: emptyList()
        }.filter { it.game.name.lowercase().contains(state.value.searchTxt.lowercase()) }

        val selectedGameList = newGameList.filter { uiState ->
            (state.value.checkboxBaseGame && !uiState.game.expansion) ||
                    (state.value.checkboxExpansionGame && uiState.game.expansion)
        }
        _state.update {
            it.copy(
                gameListEdited = selectedGameList
            )
        }
    }

    fun updateExpandedGameCover(game: Game) {
        val newGameList = state.value.gameList?.map {
            if (it.game.id == game.id) {
                it.copy(isExpanded = !it.isExpanded)
            } else {
                it
            }
        } ?: emptyList()

        _state.update {
            it.copy(
                gameList = newGameList
            )
        }
        refreshGameList()
    }

    fun changeAllExpendedGameCover() {
        val newGameList = state.value.gameList?.map {
            it.copy(isExpanded = !it.isExpanded)
        } ?: emptyList()

        _state.update {
            it.copy(
                gameList = newGameList
            )
        }
        refreshGameList()
    }


    fun refresh() {
        viewModelScope.launch {
            when (val response = gameDbRepository.getAllGame()) {
                is RequestResult.Success -> {
                    val gameUiStates = response.data.map { GameUiState(game = it) }
                    _state.update {
                        it.copy(
                            gameList = gameUiStates,
                        )
                    }
                    refreshGameList()
                }

                is RequestResult.Error -> {
                    _events.send(GameListEvent.ShowMessage(R.string.error_generic, AppSnackBarType.ERROR))
                }
            }
        }
    }

    fun validateDeleteGame(game: Game) {
        viewModelScope.launch {
            val response = gameDbRepository.deleteGame(game = game)
            when (response) {
                is RequestResult.Success -> {
                    refresh()
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    _events.send(GameListEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS))
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    _events.send(GameListEvent.ShowMessage(R.string.error_generic, AppSnackBarType.ERROR))
                }
            }
        }
    }
}