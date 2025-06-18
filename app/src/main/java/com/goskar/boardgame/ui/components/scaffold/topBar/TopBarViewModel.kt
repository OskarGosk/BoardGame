package com.goskar.boardgame.ui.components.scaffold.topBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.GetAllPlayerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TopBarState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = true,
    val questAccount: Boolean = false,
)

class TopBarViewModel(
    private val api: BoardGameFirebaseDataRepository,
    private val getAllGameDb: GetAllGameUseCase,
    private val getAllPlayerDb: GetAllPlayerUseCase,
    private val getAllHistoryDb: GetAllHistoryGameUseCase,
    ) : ViewModel() {

    private val _state = MutableStateFlow(TopBarState())
    val state = _state.asStateFlow()

    fun update(state: TopBarState) {
        _state.update { state }
    }

    fun uploadDataToFirebase() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val synchronizationResult = attemptToSendAllData(
                firstAction = ::addAllGame,
                secondAction = ::addAllPlayer,
                thirdAction = ::addAllHistory
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = synchronizationResult
                )
            }

        }
    }

    suspend fun addAllGame(): RequestResult<Boolean> {
        val allGame = getAllGameDb.invoke()
        val gameMap: Map<String, Game> = allGame.associateBy { it.id }
        val responseGame = api.addAllGame(gameMap)
        return responseGame
    }

    suspend fun addAllPlayer(): RequestResult<Boolean> {
        val allPlayer = getAllPlayerDb.invoke()
        val playerMap: Map<String, Player> = allPlayer.associateBy { it.id }
        val responsePlayer = api.addPlayer(playerMap)
        return responsePlayer
    }

    suspend fun addAllHistory(): RequestResult<Boolean> {
        val allHistory = getAllHistoryDb.invoke()
        val historyMap: Map<String, HistoryGame> = allHistory.associateBy { it.id }
        val responseHistory = api.addHistoryGame(historyMap)
        return responseHistory
    }

    private suspend fun attemptToSendAllData(
        firstAction: suspend () -> RequestResult<Boolean>,
        secondAction: suspend () -> RequestResult<Boolean>,
        thirdAction: suspend () -> RequestResult<Boolean>,
    ): Boolean {
        if (firstAction() is RequestResult.Success) {
            if (secondAction() is RequestResult.Success) {
                if (thirdAction() is RequestResult.Success) {
                    return true
                } else return false
            } else return false
        } else return false
    }
}