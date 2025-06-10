package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlayerDbRepositoryImpl(
    private val playerDao: PlayerDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlayerDbRepository {
    companion object {
        val TAG = "PLAYER"
    }

    override suspend fun insertPlayer(player: Player): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.insert(player)
            }.onFailure {
                Timber.tag(TAG).e("Can't add new player\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun insertAllPlayer(playerList: List<Player>): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.insertAll(playerList)
            }.onFailure {
                Timber.tag(TAG).e("Can't add all player\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllPlayer(): RequestResult<List<Player>> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.getAll()
            }.onFailure {
                Timber.tag(TAG).e("Can't get player list\n ${it.stackTraceToString()}")
            }
        }.fold(
            onSuccess = { RequestResult.Success(it) },
            onFailure = { RequestResult.Error(it) })
    }

    override suspend fun deletePlayer(player: Player): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.delete(player)
            }.onFailure {
                Timber.tag(TAG).e("Can't delete player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun editPlayer(player: Player): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.edit(player)
            }.onFailure {
                Timber.tag(TAG).e("Can't edit player\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun deleteAllPlayer(): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                playerDao.deleteAll()
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't delete all player\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }
}