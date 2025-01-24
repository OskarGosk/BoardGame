package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.models.Game
import timber.log.Timber

class GameDbRepositoryImpl(
    private val gameDao: GameDao
) : GameDbRepository {
    companion object {
        val TAG = "GAMES_LIST"
    }

    override suspend fun insertGame(game: Game): Boolean {
        try {
            gameDao.insert(game)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add new game\n  $e}")
            return false
        }
    }

    override suspend fun insertAllGame(gameList: List<Game>): Boolean {
        try {
            gameDao.insertAll(gameList)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't add all game from list\n  $e}")
            return false
        }
    }

    override suspend fun getAllGame(): List<Game> {
        try {
            return gameDao.getAll()
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't get all game list\n  $e}")
            return emptyList()
        }
    }

    override suspend fun deleteGame(game: Game): Boolean {
        try {
            gameDao.delete(game)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't delete game\n  $e}")
            return false
        }
    }

    override suspend fun editGame(game: Game): Boolean {
        try {
            gameDao.edit(game)
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e("Can't edit game\n  $e}")
            return false
        }
    }
}