package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.safeCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameDbRepositoryImpl(
    private val gameDao: GameDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GameDbRepository {
    companion object {
        const val TAG = "GAMES_LIST"
    }

    override suspend fun insertGame(game: Game): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add game") {
            gameDao.insert(game)
            true
        }
    }


        override suspend fun insertAllGame(gameList: List<Game>): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add all game from list") {
            gameDao.insertAll(gameList)
            true
        }
    }


        override suspend fun getAllGame(): RequestResult<List<Game>> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't get all game") {
            gameDao.getAll()
        }
    }

    override suspend fun deleteGame(game: Game): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete game") {
            gameDao.delete(game)
            true
        }
    }

    override suspend fun editGame(game: Game): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't edit game") {
            gameDao.edit(game)
            true
        }
    }

    override suspend fun deleteAllGame(): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete all game") {
            gameDao.deleteAll()
            true
        }
    }
}