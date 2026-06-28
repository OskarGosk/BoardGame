package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.safeCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerDbRepositoryImpl(
    private val playerDao: PlayerDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlayerDbRepository {
    companion object {
        const val TAG = "PLAYER"
    }

    override suspend fun insertPlayer(player: Player): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add new player") {
            playerDao.insert(player)
            true
        }
    }

    override suspend fun insertAllPlayer(playerList: List<Player>): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add all player") {
            playerDao.insertAll(playerList)
            true
        }
    }

    override suspend fun getAllPlayer(): RequestResult<List<Player>> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't get player list") {
            playerDao.getAll()
        }
    }

    override suspend fun deletePlayer(player: Player): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete player") {
            playerDao.delete(player)
            true
        }
    }

    override suspend fun editPlayer(player: Player): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't edit player") {
            playerDao.edit(player)
            true
        }
    }

    override suspend fun deleteAllPlayer(): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't delete all player") {
            playerDao.deleteAll()
            true
        }
    }
}
