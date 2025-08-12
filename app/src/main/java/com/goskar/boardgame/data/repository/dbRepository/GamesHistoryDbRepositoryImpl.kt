package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.db.HistoryGameExpansionDao
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class GamesHistoryDbRepositoryImpl(
    private val historyGameDao: HistoryGameDao,
    private val historyGameExpansionDao: HistoryGameExpansionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GamesHistoryDbRepository {
    companion object {
        const val TAG = "GAMES_HISTORY"
    }

    override suspend fun insertHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.insert(historyGame)
            }.onFailure {
                Timber.tag(TAG).e("Can't add game history\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.insertAll(historyGameList)
            }.onFailure {
                Timber.tag(TAG).e("Can't add All games history\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllHistoryGame(): RequestResult<List<HistoryGame>> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.getAll()
            }.onFailure {
                Timber.tag(TAG).e("Can't get all games history\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun deleteHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.delete(historyGame)
            }.onFailure {
                Timber.tag(TAG).e("Can't delete game history\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun editHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.edit(historyGame)
            }.onFailure {
                Timber.tag(TAG).e("Can't edit game history\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun deleteAllHistory(): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameDao.deleteAll()
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't delete all history\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun insertHistoryGameExpansion(expansion: HistoryGameExpansion): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameExpansionDao.insertExpansion(expansion)
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't add game expansion history\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun insertAllHistoryGameExpansion(expansion: List<HistoryGameExpansion>): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameExpansionDao.insertAllExpansion(expansion)
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't add game expansion history\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllHistoryGameExpansion(): RequestResult<List<HistoryGameExpansion>> {
        return withContext(defaultDispatcher) {
            runCatching {
                historyGameExpansionDao.getAll()
            }.onFailure {
                Timber.tag(TAG).e("Can't get all games history\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }
}