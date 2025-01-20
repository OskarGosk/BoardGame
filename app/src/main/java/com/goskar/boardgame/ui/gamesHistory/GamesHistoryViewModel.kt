package com.goskar.boardgame.ui.gamesHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.HistoryGameNetworkRepository
import com.goskar.boardgame.data.rest.models.HistoryGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GamesHistoryState(
    val historyList: List<HistoryGame> = emptyList(),
    val errorVisible: Boolean = false,
    val searchTxt: String = ""

)

@KoinViewModel
class GamesHistoryViewModel(
    private val historyGameNetworkRepository: HistoryGameNetworkRepository
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
            val response = historyGameNetworkRepository.getAll().toMutableList()
            _state.update {
                it.copy(
                    historyList = response
                )
            }
        }
    }
}