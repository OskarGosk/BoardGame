package com.goskar.boardgame.ui.gameSearchBGG

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.SearchBGGList
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GameSearchState(
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val isLoading: Boolean = false,
    val error: Boolean = false
)

@KoinViewModel
class GameSearchViewModel(
    private val boardGameApiRepository: BoardGameApiRepository
): ViewModel() {

    private val _gameList = MutableStateFlow<SearchBGGList?>(null)
    val gameList = _gameList.asStateFlow()

    private val _state = MutableStateFlow(GameSearchState())
    val state = _state.asStateFlow()

    fun update(state: GameSearchState) {
        _state.update { state }
    }

    init {
        searchGame("Marvel")
    }

    fun  searchGame(name: String) {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            when(val response = boardGameApiRepository.searchGame(name)) {
                is RequestResult.Success -> {
                    _gameList.value = response.data
                }
                is RequestResult.Error -> {
                    emptyList<Game>()
                    _state.update {
                        it.copy(
                            error = true
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