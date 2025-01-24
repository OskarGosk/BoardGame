package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.models.Game

class GameDbRepositoryImpl(
    private val gameDao: GameDao
) : GameDbRepository {
    override suspend fun insertGame(game: Game) {
        gameDao.insert(game)
    }

    override suspend fun insertAllGame(gameList: List<Game>) {
        gameDao.insertAll(gameList)
    }

    override suspend fun getAllGame(): List<Game> {
        return gameDao.getAll()
    }

    override suspend fun deleteGame(game: Game) {
        gameDao.delete(game)
    }

    override suspend fun editGame(game: Game) {
        gameDao.edit(game)
    }
}