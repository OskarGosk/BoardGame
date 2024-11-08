package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.GameIdRespons


interface GameNetworkRepository {

    suspend fun addGame(game: Game): GameIdRespons
    suspend fun getAllGame(): List<Game>
    suspend fun deleteGame(gameId: String): RequestResult<Boolean>
    suspend fun editGame(game: Game): RequestResult<Boolean>

}