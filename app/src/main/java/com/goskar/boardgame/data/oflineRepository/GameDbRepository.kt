package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Game

interface GameDbRepository {

    suspend fun insertGame(game: Game) : Boolean

    suspend fun insertAllGame(gameList: List<Game>) : Boolean

    suspend fun getAllGame(): List<Game>

    suspend fun deleteGame(game: Game) : Boolean

    suspend fun editGame(game: Game) : Boolean
}
