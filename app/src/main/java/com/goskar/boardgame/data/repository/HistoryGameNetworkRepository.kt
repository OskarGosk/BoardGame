package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse

interface HistoryGameNetworkRepository {

    suspend fun addHistoryGame(historyGame: HistoryGame): HistoryIdResponse
    suspend fun getAll(): List<HistoryGame>
    suspend fun deleteHistoryGame (historyGameId: String)
    suspend fun editHistoryGame (historyGame: HistoryGame)
}