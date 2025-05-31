package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.RequestResult

interface GameDbRepository {

    suspend fun insertGame(game: Game): RequestResult<Boolean>

    suspend fun insertAllGame(gameList: List<Game>): RequestResult<Boolean>

    suspend fun getAllGame(): RequestResult<List<Game>>

    suspend fun deleteGame(game: Game): RequestResult<Boolean>

    suspend fun editGame(game: Game): RequestResult<Boolean>
}
