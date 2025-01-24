package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryIdResponse


interface HistoryGameNetworkRepository {

    suspend fun addHistoryGame(historyGame: HistoryGame): RequestResult<Boolean>
    suspend fun getAll(): List<HistoryGame>
    suspend fun deleteHistoryGame (historyGameId: String)
    suspend fun editHistoryGame (historyGame: HistoryGame)
}