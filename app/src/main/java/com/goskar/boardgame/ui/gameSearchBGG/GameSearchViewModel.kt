package com.goskar.boardgame.ui.gameSearchBGG

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.SearchList
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GameSearchState(
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
)

@KoinViewModel
class GameSearchViewModel(
    private val boardGameApiRepository: BoardGameApiRepository
): ViewModel() {

    private val _gameList = MutableStateFlow<SearchList?>(null)
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
        viewModelScope.launch {
            val response = boardGameApiRepository.searchGame(name)
            _gameList.value = response
            Log.d("Oskar22","GRY --- $response")
        }
    }
}