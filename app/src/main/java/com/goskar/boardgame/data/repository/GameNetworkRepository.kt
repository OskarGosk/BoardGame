package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Game


interface GameNetworkRepository {

    suspend fun addGame(game: Game): RequestResult<Boolean>
    suspend fun getAllGame(): List<Game>
    suspend fun deleteGame(gameId: String): RequestResult<Boolean>
    suspend fun editGame(game: Game): RequestResult<Boolean>

}