package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.GameIdRespons

class GameNetworkRepositoryImpl(
    private val apiBoardGame: ApiBoardGame,
) : GameNetworkRepository {


    override suspend fun addGame(game: Game): GameIdRespons {
        return apiBoardGame.addGame(game)
    }

    override suspend fun getAllGame(): List<Game> {
        return apiBoardGame.getAllGame().map { element ->
            element.value.copy(id = element.key)
        }
    }

    override suspend fun deleteGame(gameId: String) {
        apiBoardGame.deleteGame(gameId)
    }

    override suspend fun editGame(game: Game) {
        apiBoardGame.editGame(game.id, game)
    }

}