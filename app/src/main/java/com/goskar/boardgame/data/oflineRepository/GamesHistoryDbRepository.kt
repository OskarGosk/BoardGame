package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.HistoryGame

interface GamesHistoryDbRepository {
    suspend fun insertHistoryGame(historyGame: HistoryGame)

    suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>)

    suspend fun getAllHistoryGame(): List<HistoryGame>

    suspend fun deleteHistoryGame(historyGame: HistoryGame)

    suspend fun editHistoryGame(historyGame: HistoryGame)
}