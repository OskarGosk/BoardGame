package com.goskar.boardgame.ui.gameSearchBGG

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.SearchList
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class GameSearchViewModel(
    private val boardGameApiRepository: BoardGameApiRepository
): ViewModel() {

    private val _gameList = MutableStateFlow<SearchList?>(null)
    val gameList = _gameList.asStateFlow()

    fun  searchGame(name: String) {
        viewModelScope.launch {
            val response = boardGameApiRepository.searchGame(name)
            _gameList.value = response
        }
    }
}