package com.goskar.boardgame.ui.gameSearchBGG

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.SearchBGGListElements
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

    private val _gameList = MutableStateFlow<List<SearchBGGListElements>?>(null)
    val gameList = _gameList.asStateFlow()

    private val _gameListSorted = MutableStateFlow<List<SearchBGGListElements>?>(null)
    val gameListSorted = _gameListSorted.asStateFlow()

    private val _state = MutableStateFlow(GameSearchState())
    val state = _state.asStateFlow()

    fun update(state: GameSearchState) {
        _state.update { state }
    }

    init {
        searchGame("Marvel")
    }

    fun updateSortedList() {
        _gameListSorted.value = when (_state.value.sortOption) {
            R.string.default_sort -> _gameList.value
            R.string.name_ascending -> _gameList.value?.sortedBy { it.name }
            R.string.name_descending -> _gameList.value?.sortedByDescending { it.name }
            R.string.played_ascending -> _gameList.value?.sortedBy { it.yearPublished }
            R.string.played_descending -> _gameList.value?.sortedByDescending { it.yearPublished }
            else -> _gameList.value
        }?.filter { it.name?.lowercase()?.contains(_state.value.searchTxt.lowercase()) == true }
    }


    fun  searchGame(name: String) {
        _state.update {
            it.copy(
                isLoading = true,
                sortOption = R.string.default_sort,
            )
        }
        viewModelScope.launch {
            when(val response = boardGameApiRepository.searchGame(name)) {
                is RequestResult.Success -> {
                    _gameList.value = response.data.boardGames
                    updateSortedList()
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