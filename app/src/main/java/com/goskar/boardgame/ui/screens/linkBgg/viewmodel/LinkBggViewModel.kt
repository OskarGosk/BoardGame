package com.goskar.boardgame.ui.screens.linkBgg.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.bgg.BoardGameApiRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.screens.addGame.viewmodel.PopularMatch
import com.goskar.boardgame.ui.screens.addGame.viewmodel.toPopularMatches
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LinkBggState(
    val gameName: String = "",
    val query: String = "",
    val isSearching: Boolean = false,
    val searched: Boolean = false,
    val matches: List<PopularMatch> = emptyList(),
)

sealed interface LinkBggEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : LinkBggEvent
}

class LinkBggViewModel(
    private val boardGameApiRepository: BoardGameApiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LinkBggState())
    val state = _state.asStateFlow()

    private val _events = Channel<LinkBggEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var searchJob: Job? = null

    // The screen is recomposed when the user comes back from the details preview; keep their results.
    fun start(gameName: String) {
        if (_state.value.gameName.isNotEmpty()) return
        _state.update { it.copy(gameName = gameName, query = gameName) }
        search()
    }

    fun updateQuery(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun search() {
        val query = _state.value.query.trim()
        searchJob?.cancel()
        if (query.length < 2) {
            _state.update { it.copy(matches = emptyList(), isSearching = false, searched = false) }
            return
        }
        searchJob = viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            when (val response = boardGameApiRepository.searchGame(query)) {
                is RequestResult.Success -> _state.update {
                    it.copy(matches = response.data.toPopularMatches(), isSearching = false, searched = true)
                }

                is RequestResult.Error -> {
                    _state.update { it.copy(matches = emptyList(), isSearching = false, searched = false) }
                    _events.send(LinkBggEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                }
            }
        }
    }
}
