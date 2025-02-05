package com.goskar.boardgame.ui.gamesList.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GameListState(
    val gameList: List<Game>? = emptyList<Game>(),
    val successDeleteGame: Boolean = false,
    val errorVisible: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort

)

@KoinViewModel
class GameListViewModel(
    private val gameDbRepository: GameDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GameListState())
    val state = _state.asStateFlow()

    fun update(state: GameListState) {
        _state.update { state }
    }

    init {
        getAllGame()
    }

    fun getAllGame() {
        viewModelScope.launch {
            val response = gameDbRepository.getAllGame().toMutableList()
            _state.update {
                it.copy(
                    gameList = response
                )
            }
        }
    }

    fun validateDeleteGame(game: Game){
        viewModelScope.launch {
            val response = gameDbRepository.deleteGame(game = game)
            when (response){
                true -> {
                    _state.update {
                        it.copy(
                            successDeleteGame = true
                        )
                    }
                    getAllGame()
                }
                false -> {
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