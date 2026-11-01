package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.db.SyncDao
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.safeCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerDbRepositoryImpl(
    private val playerDao: PlayerDao,
    private val syncDao: SyncDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = System::currentTimeMillis,
) : PlayerDbRepository {
    companion object {
        const val TAG = "PLAYER"
    }

    override suspend fun insertPlayer(player: Player): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't add new player") {
            playerDao.insert(player.copy(updatedAt = clock()))
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
            syncDao.insertDeleted(DeletedRecord(player.id, DeletedRecord.PLAYER, clock()))
            true
        }
    }

    override suspend fun editPlayer(player: Player): RequestResult<Boolean> = withContext(defaultDispatcher) {
        safeCall(TAG, "Can't edit player") {
            playerDao.edit(player.copy(updatedAt = clock()))
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
