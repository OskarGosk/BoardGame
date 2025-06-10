package com.goskar.boardgame.ui.firebaseData

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
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DataFromFirebaseState(
    val keyVerify: Boolean? = false,
    val keyValue: String? = "",
)

class DataFromFirebaseViewModel(
    private val api: BoardGameFirebaseDataRepository,
    private val getAllGameDb: GetAllGameUseCase,
    private val getAllPlayerDb: GetAllPlayerUseCase,
    private val getAllHistoryDb: GetAllHistoryGameUseCase,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DataFromFirebaseState())
    val state = _state.asStateFlow()

    fun update(state: DataFromFirebaseState) {
        _state.update {
            state
        }
    }

    fun uploadDataToFirebase() {
        viewModelScope.launch {
            val allGame = getAllGameDb.invoke()
            val gameMap: Map<String, Game> = allGame.associateBy { it.id }
            api.addAllGame(gameMap)

            val allPlayer = getAllPlayerDb.invoke()
            val playerMap: Map<String, Player> = allPlayer.associateBy { it.id }
            api.addPlayer(playerMap)

            val allHistory = getAllHistoryDb.invoke()
            val historyMap: Map<String, HistoryGame> = allHistory.associateBy { it.id }
            api.addHistoryGame(historyMap)
        }
    }

    fun downloadDataFromFirebase() {
        viewModelScope.launch {
            val allGame = api.getAllGame()
            if (allGame is RequestResult.Success) {
                addAllGameToDb.invoke(allGame.data)
            }
            val allPlayer = api.getAllPlayer()
            if(allPlayer is RequestResult.Success) {
                addAllPlayerToDb.invoke(allPlayer.data)
            }
            val allHistory = api.getAllHistoryGame()
            if(allHistory is RequestResult.Success) {
                addAllHistoryToDb.invoke(allHistory.data)
            }
        }
    }
}