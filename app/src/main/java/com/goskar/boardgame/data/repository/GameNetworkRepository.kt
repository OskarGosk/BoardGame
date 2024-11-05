package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.GameIdRespons

interface GameNetworkRepository {

    suspend fun addGame(game: Game): GameIdRespons
    suspend fun getAllGame(): List<Game>
    suspend fun deleteGame(gameId: String)
    suspend fun editGame(game: Game)

}