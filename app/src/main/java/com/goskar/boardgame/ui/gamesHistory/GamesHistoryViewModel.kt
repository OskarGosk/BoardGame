package com.goskar.boardgame.ui.gamesHistory

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameHistoryEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GameHistoryEvent
}
data class GamesHistoryState(
    val historyList: List<HistoryGame> = emptyList(),
    val historyGameWithExpansion: List<HistoryGameWithExpansion> = emptyList(),
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val loading: Boolean = true
)

class GamesHistoryViewModel(
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getHistoryWithExpansionUseCase: GetHistoryWithExpansionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GamesHistoryState())
    val state = _state.asStateFlow()

    private val _events = Channel<GameHistoryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        getAllHistoryGame()
        validateGetHistoryGameWithExpansion()
    }

    fun updateSearchTxt(value: String) {
        _state.update { it.copy(searchTxt = value) }
    }

    fun updateSortOption(value: Int) {
        _state.update { it.copy(sortOption = value) }
    }

    fun getAllHistoryGame() {
        viewModelScope.launch {
            val response = gamesHistoryDbRepository.getAllHistoryGame()
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            historyList = response.data.sortedBy { it.gameData },
                            loading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }
                }
            }

        }
    }

    private fun validateGetHistoryGameWithExpansion() {
        viewModelScope.launch {
            when (val response = getHistoryWithExpansionUseCase.invoke()) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            historyGameWithExpansion = response.data.sortedBy { it.history.gameData },
                            loading = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }
                }
            }
        }
    }
}