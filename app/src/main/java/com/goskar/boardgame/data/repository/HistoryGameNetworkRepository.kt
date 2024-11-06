package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.data.rest.models.HistoryIdResponse


interface HistoryGameNetworkRepository {

    suspend fun addHistoryGame(historyGame: HistoryGame): HistoryIdResponse
    suspend fun getAll(): List<HistoryGame>
    suspend fun deleteHistoryGame (historyGameId: String)
    suspend fun editHistoryGame (historyGame: HistoryGame)
}