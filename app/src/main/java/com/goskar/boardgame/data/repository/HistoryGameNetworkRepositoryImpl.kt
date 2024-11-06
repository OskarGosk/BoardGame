package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.data.rest.models.HistoryIdResponse

class HistoryGameNetworkRepositoryImpl(
    private val apiBoardGame : ApiBoardGame,
): HistoryGameNetworkRepository {

    override suspend fun addHistoryGame(historyGame: HistoryGame): HistoryIdResponse {
        return apiBoardGame.addHistory(historyGame)
    }

    override suspend fun getAll(): List<HistoryGame>{
        return apiBoardGame.getAllHistoryGame().map { element ->
            element.value.copy(id = element.key)
        }
    }

    override suspend fun deleteHistoryGame (historyGameId: String){
        apiBoardGame.deleteHistoryGame(historyGameId)
    }

    override suspend fun editHistoryGame (historyGame: HistoryGame) {
        apiBoardGame.editHistoryGame(historyGame.id, historyGame)
    }
}