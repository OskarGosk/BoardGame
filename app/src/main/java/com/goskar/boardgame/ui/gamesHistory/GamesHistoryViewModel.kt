package com.goskar.boardgame.ui.gamesHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.oflineRepository.GamesHistoryDbRepository
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

    private fun getAllHistoryGame() {
        viewModelScope.launch {
            val response = gamesHistoryDbRepository.getAllHistoryGame()
            when (response) {
                is RequestResult.SuccessWithData -> {
                    _state.update {
                        it.copy(
                            historyList = response.data as List<HistoryGame>,
                            errorVisible = false,
                            loading = false
                        )
                    }
                }
                else -> {
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