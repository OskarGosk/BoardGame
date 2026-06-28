package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.db.HistoryGameExpansionDao
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.safeCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GamesHistoryDbRepositoryImpl(
    private val historyGameDao: HistoryGameDao,
    private val historyGameExpansionDao: HistoryGameExpansionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GamesHistoryDbRepository {
    companion object {
        const val TAG = "GAMES_HISTORY"
    }

    override suspend fun insertHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add game history") {
            historyGameDao.insert(historyGame)
            true
        }
    }

    override suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add all games history") {
            historyGameDao.insertAll(historyGameList)
            true
        }
    }

    override suspend fun getAllHistoryGame(): RequestResult<List<HistoryGame>> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't get all games history") {
            historyGameDao.getAll()
        }
    }

    override suspend fun deleteHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete game history") {
            historyGameDao.delete(historyGame)
            true
        }
    }

    override suspend fun editHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't edit game history") {
            historyGameDao.edit(historyGame)
            true
        }
    }

    override suspend fun deleteAllHistory(): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete all history") {
            historyGameDao.deleteAll()
            true
        }
    }

    override suspend fun insertHistoryGameExpansion(expansion: HistoryGameExpansion): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add game expansion history") {
            historyGameExpansionDao.insertExpansion(expansion)
            true
        }
    }

    override suspend fun insertAllHistoryGameExpansion(expansion: List<HistoryGameExpansion>): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add all game expansion history") {
            historyGameExpansionDao.insertAllExpansion(expansion)
            true
        }
    }

    override suspend fun getAllHistoryGameExpansion(): RequestResult<List<HistoryGameExpansion>> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't get all game expansion history") {
            historyGameExpansionDao.getAll()
        }
    }

    override suspend fun deleteAllHistoryExpansion(): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete all expansion history") {
            historyGameExpansionDao.deleteAll()
            true
        }
    }
}
