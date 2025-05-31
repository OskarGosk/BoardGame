package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class GameDbRepositoryImpl(
    private val gameDao: GameDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GameDbRepository {
    companion object {
        val TAG = "GAMES_LIST"
    }

    override suspend fun insertGame(game: Game): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                gameDao.insert(game)
            }.onFailure {
                Timber.tag(TAG).e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }


    override suspend fun insertAllGame(gameList: List<Game>): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                gameDao.insertAll(gameList)
            }.onFailure {
                Timber.tag(TAG)
                    .e("Can't add all game from list\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllGame(): RequestResult<List<Game>> {
        return withContext(defaultDispatcher) {
            runCatching {
                gameDao.getAll()
            }.onFailure {
                Timber.tag(TAG)
                    .e("Can't add all game from list\n ${it.stackTraceToString()}")
            }
        }.fold(
            onSuccess = { RequestResult.Success(it) },
            onFailure = { RequestResult.Error(it) })
    }

    override suspend fun deleteGame(game: Game): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                gameDao.delete(game)
            }.onFailure {
                Timber.tag(TAG).e("Can't delete game\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun editGame(game: Game): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                gameDao.edit(game)
            }.onFailure {
                Timber.tag(TAG).e("Can't edit game\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }
}