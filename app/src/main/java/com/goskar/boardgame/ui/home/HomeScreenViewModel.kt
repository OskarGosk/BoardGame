package com.goskar.boardgame.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isSuccessDownloadData: Boolean = false
)

@KoinViewModel
class HomeScreenViewModel(
    private val api: BoardGameFirebaseDataRepository,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    var state = _state.asStateFlow()


    fun getAllData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccessDownloadData = attemptToTakeAllData(
                        firstAction = ::getAllGame,
                        secondAction = ::getAllPlayer,
                        thirdAction = ::getAllHistory
                    )
                )
            }
        }
    }

    suspend fun getAllGame(): Boolean {
        val allGame = api.getAllGame()

        if (allGame is RequestResult.Success) {
            val response = addAllGameToDb.invoke(allGame.data)
            return response
        } else return false
    }

    suspend fun getAllPlayer(): Boolean {
        val allPlayer = api.getAllPlayer()

        if (allPlayer is RequestResult.Success) {
            val response = addAllPlayerToDb.invoke(allPlayer.data)
            return response
        } else return false
    }

    suspend fun getAllHistory(): Boolean {
        val allHistory = api.getAllHistoryGame()

        if (allHistory is RequestResult.Success) {
            val response = addAllHistoryToDb.invoke(allHistory.data)
            return response
        } else return false
    }

    private suspend fun attemptToTakeAllData(
        firstAction: suspend () -> Boolean,
        secondAction: suspend () -> Boolean,
        thirdAction: suspend () -> Boolean,
    ): Boolean {
        return if (firstAction()) {
            if (secondAction()) {
                thirdAction()
            } else false
        } else false
    }

}