package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Game

interface GameDbRepository {

    suspend fun insertGame(game: Game)

    suspend fun insertAllGame(gameList: List<Game>)

    suspend fun getAllGame(): List<Game>

    suspend fun deleteGame(game: Game)

    suspend fun editGame(game: Game)
}
