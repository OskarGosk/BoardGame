package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.models.HistoryGame
import timber.log.Timber


class GamesHistoryDbRepositoryImpl(
    private val historyGameDao: HistoryGameDao
) : GamesHistoryDbRepository {
    companion object {
        val TAG = "GAMES_HISTORY"
    }

    override suspend fun insertHistoryGame(historyGame: HistoryGame): Boolean {
        try {
            historyGameDao.insert(historyGame)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add game history\n  $e}")
            return false
        }
    }

    override suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>): Boolean {
        try {
            historyGameDao.insertAll(historyGameList)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add All games history\n  $e}")
            return false
        }

    }

    override suspend fun getAllHistoryGame(): List<HistoryGame> {
        try {
            return historyGameDao.getAll()
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't get all games history\n  $e}")
            return emptyList()
        }

    }

    override suspend fun deleteHistoryGame(historyGame: HistoryGame): Boolean {
        try {
            historyGameDao.delete(historyGame)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't delete game history\n  $e}")
            return false
        }
    }

    override suspend fun editHistoryGame(historyGame: HistoryGame): Boolean {
        try {
            historyGameDao.edit(historyGame)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't edit game history\n  $e}")
            return false
        }

    }
}