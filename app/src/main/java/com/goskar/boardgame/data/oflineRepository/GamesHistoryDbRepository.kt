package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.HistoryGame

interface GamesHistoryDbRepository {
    suspend fun insertHistoryGame(historyGame: HistoryGame) : Boolean

    suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>) : Boolean

    suspend fun getAllHistoryGame(): List<HistoryGame>

    suspend fun deleteHistoryGame(historyGame: HistoryGame) : Boolean

    suspend fun editHistoryGame(historyGame: HistoryGame) : Boolean
}