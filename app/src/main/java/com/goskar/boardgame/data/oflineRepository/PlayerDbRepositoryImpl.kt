package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.models.Player

class PlayerDbRepositoryImpl(
    private val playerDao: PlayerDao
): PlayerDbRepository {
    override suspend fun insertPlayer(player: Player) {
        playerDao.insert(player)
    }

    override suspend fun insertAllPlayer(playerList: List<Player>) {
        playerDao.insertAll(playerList)
    }

    override suspend fun getAllPlayer(): List<Player> {
        return playerDao.getAll()
    }

    override suspend fun deletePlayer(player: Player) {
        playerDao.delete(player)
    }

    override suspend fun editPlayer(player: Player) {
        playerDao.edit(player)
    }
}