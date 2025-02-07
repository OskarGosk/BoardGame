package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.RequestResult

interface GameDbRepository {

    suspend fun insertGame(game: Game): RequestResult<Boolean>

    suspend fun insertAllGame(gameList: List<Game>): RequestResult<Boolean>

    suspend fun getAllGame(): RequestResult<Boolean>

    suspend fun deleteGame(game: Game): RequestResult<Boolean>

    suspend fun editGame(game: Game): RequestResult<Boolean>
}
