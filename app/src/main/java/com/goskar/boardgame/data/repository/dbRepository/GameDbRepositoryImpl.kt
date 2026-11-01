package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.db.SyncDao
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.safeCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameDbRepositoryImpl(
    private val gameDao: GameDao,
    private val syncDao: SyncDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = System::currentTimeMillis,
) : GameDbRepository {
    companion object {
        const val TAG = "GAMES_LIST"
    }

    override suspend fun insertGame(game: Game): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add game") {
            gameDao.insert(game.copy(updatedAt = clock()))
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
            syncDao.insertDeleted(DeletedRecord(game.id, DeletedRecord.GAME, clock()))
            true
        }
    }

    override suspend fun editGame(game: Game): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't edit game") {
            gameDao.edit(game.copy(updatedAt = clock()))
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