package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse
import android.content.Context
import com.google.gson.Gson
import com.goskar.boardgame.data.rest.ApiBoardGame

class HistoryGameNetworkRepositoryImpl(
    private val apiBoardGame : ApiBoardGame,
    private val gson: Gson,
    private val context: Context
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