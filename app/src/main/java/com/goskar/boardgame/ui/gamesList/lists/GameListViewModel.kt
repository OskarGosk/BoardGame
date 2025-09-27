package com.goskar.boardgame.ui.gamesList.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GameListState(
    val gameList: List<GameUiState>? = emptyList(),
    val gameListEdited: List<GameUiState> = emptyList(),
    val successDeleteGame: Boolean = false,
    val errorVisible: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val isLoading: Boolean = false,
    val checkboxBaseGame: Boolean = true,
    val checkboxExpansionGame: Boolean = true,
    val checkboxAllGame: Boolean = true
)

data class GameUiState(
    val game: Game,
    val isExpanded: Boolean = true
)

@KoinViewModel
class GameListViewModel(
    private val gameDbRepository: GameDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(GameListState())
    val state = _state.asStateFlow()

    fun update(state: GameListState) {
        _state.update { state }
    }

    init {
        refresh()
    }

    fun refreshGameList() {
        val newGameList: List<GameUiState> = when (state.value.sortOption) {
            R.string.default_sort -> state.value.gameList ?: emptyList()
            R.string.name_ascending -> state.value.gameList?.sortedBy { it.game.name }
                ?: emptyList()

            R.string.name_descending -> state.value.gameList?.sortedByDescending { it.game.name }
                ?: emptyList()

            R.string.played_ascending -> state.value.gameList?.sortedBy { it.game.games }
                ?: emptyList()

            R.string.played_descending -> state.value.gameList?.sortedByDescending { it.game.games }
                ?: emptyList()

            else -> state.value.gameList ?: emptyList()
        }.filter { it.game.name.lowercase().contains(state.value.searchTxt.lowercase()) }

        val selectedGameList = buildList {
            if (state.value.checkboxBaseGame) {
                addAll(newGameList.filter { !it.game.expansion })
            }
            if (state.value.checkboxExpansionGame) {
                addAll(newGameList.filter { it.game.expansion })
            }
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

            val games = getAllGameUseCase.invoke()
            val gameUiStates = games.map { GameUiState(game = it) }
            _state.update {
                it.copy(
                    gameList = gameUiStates
                )
            }
            refreshGameList()
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
                            errorVisible = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }
}