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
    val gameList: List<Game>? = emptyList(),
    val gameListEdited: List<Game> = emptyList(),
    val successDeleteGame: Boolean = false,
    val errorVisible: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val isLoading: Boolean = false,
    val checkboxBaseGame: Boolean = true,
    val checkboxExpansionGame: Boolean = true,
    val checkboxAllGame: Boolean = true
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
        val newGameList: List<Game> = when (state.value.sortOption) {
            R.string.default_sort -> state.value.gameList ?: emptyList()
            R.string.name_ascending -> state.value.gameList?.sortedBy { it.name } ?: emptyList()
            R.string.name_descending -> state.value.gameList?.sortedByDescending { it.name }
                ?: emptyList()

            R.string.played_ascending -> state.value.gameList?.sortedBy { it.games } ?: emptyList()
            R.string.played_descending -> state.value.gameList?.sortedByDescending { it.games }
                ?: emptyList()

            else -> state.value.gameList ?: emptyList()
        }.filter { it.name.lowercase().contains(state.value.searchTxt.lowercase()) }

        val selectedGameList = buildList {
            if (state.value.checkboxBaseGame) {
                addAll(newGameList.filter { !it.expansion })
            }
            if (state.value.checkboxExpansionGame) {
                addAll(newGameList.filter { it.expansion })
            }
        }
        _state.update {
            it.copy(
                gameListEdited = selectedGameList
            )
        }
    }


    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    gameList = getAllGameUseCase.invoke()
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