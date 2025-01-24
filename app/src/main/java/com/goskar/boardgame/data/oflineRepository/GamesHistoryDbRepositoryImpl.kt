package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.models.HistoryGame

class GamesHistoryDbRepositoryImpl(
    private val historyGameDao: HistoryGameDao
): GamesHistoryDbRepository {
    override suspend fun insertHistoryGame(historyGame: HistoryGame) {
        historyGameDao.insert(historyGame)
    }

    override suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>) {
        historyGameDao.insertAll(historyGameList)
    }

    override suspend fun getAllHistoryGame(): List<HistoryGame> {
        return historyGameDao.getAll()
    }

    override suspend fun deleteHistoryGame(historyGame: HistoryGame) {
        historyGameDao.delete(historyGame)
    }

    override suspend fun editHistoryGame(historyGame: HistoryGame) {
        historyGameDao.edit(historyGame)
    }
}