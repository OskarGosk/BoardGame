package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.models.Player
import timber.log.Timber

class PlayerDbRepositoryImpl(
    private val playerDao: PlayerDao
): PlayerDbRepository {
    companion object {
        val TAG = "PLAYER"
    }
    override suspend fun insertPlayer(player: Player): Boolean {
        try {
            playerDao.insert(player)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add new player\n  $e}")
            return false
        }
    }

    override suspend fun insertAllPlayer(playerList: List<Player>) : Boolean{
        try {
            playerDao.insertAll(playerList)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add all player\n  $e}")
            return false
        }
    }

    override suspend fun getAllPlayer(): List<Player> {
        try {
            return playerDao.getAll()
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't get player list\n  $e}")
            return emptyList()
        }
    }

    override suspend fun deletePlayer(player: Player) : Boolean{
        try {
            playerDao.delete(player)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't delete player\n  $e}")
            return false
        }
    }

    override suspend fun editPlayer(player: Player) : Boolean{
        try {
            playerDao.edit(player)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't edit player\n  $e}")
            return false
        }
    }
}