package com.goskar.boardgame.ui.gamesHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GamesHistoryState(
    val historyList: List<HistoryGame> = emptyList(),
    val errorVisible: Boolean = false,
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val loading: Boolean = true
)

@KoinViewModel
class GamesHistoryViewModel(
    private val gamesHistoryDbRepository: GamesHistoryDbRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GamesHistoryState())
    val state = _state.asStateFlow()

    init {
        getAllHistoryGame()
    }

    fun update(state: GamesHistoryState) {
        _state.update { state }
    }

    fun getAllHistoryGame() {
        viewModelScope.launch {
            val response = gamesHistoryDbRepository.getAllHistoryGame()
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            historyList = response.data.sortedBy { it.gameData },
                            errorVisible = false,
                            loading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            errorVisible = true,
                            loading = false
                        )
                    }
                }

            }

        }
    }
}