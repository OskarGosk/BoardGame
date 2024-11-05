package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.GameIdRespons
import android.content.Context
import com.google.gson.Gson
import com.goskar.boardgame.data.rest.ApiBoardGame

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