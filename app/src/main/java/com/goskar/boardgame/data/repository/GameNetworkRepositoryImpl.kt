package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.GameIdRespons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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

    override suspend fun deleteGame(gameId: String): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.deleteGame(gameId)
                when {
                    response.isSuccessful ->{
                        RequestResult.Success(true)
                    }
                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add player").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = {it}, onFailure = {RequestResult.Error(it)})
    }

    override suspend fun editGame(game: Game): RequestResult<Boolean> {

        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.editGame(game.id, game)
                when {
                    response.isSuccessful ->{
                        RequestResult.Success(true)
                    }
                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add player").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = {it}, onFailure = {RequestResult.Error(it)})
    }

}