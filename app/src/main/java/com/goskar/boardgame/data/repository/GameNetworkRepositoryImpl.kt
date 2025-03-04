package com.goskar.boardgame.data.repository


import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class GameNetworkRepositoryImpl(
    private val apiBoardGame: ApiBoardGame,
) : GameNetworkRepository {

    companion object {
        val TAG = "GAME"
    }


    override suspend fun addGame(game: Game): RequestResult<Boolean>{
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.addGame(game)
                when {
                    response.isSuccessful ->{
                        RequestResult.Success(true)
                    }
                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add game").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = {it}, onFailure = {RequestResult.Error(it)})
    }

    override suspend fun getAllGame(): List<Game> {
        return withContext(Dispatchers.IO) {
                runCatching {
                    apiBoardGame.getAllGame().map { element ->
                        element.value.copy(id = element.key)
                    }
                }.onFailure {
                    Timber.tag(TAG).e("Can't get All game\n  ${it.stackTraceToString()}")
                }
        }.fold(onSuccess = {it}, onFailure = { emptyList() })
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
                Timber.tag("Game").e("Can't delete player\n  ${it.stackTraceToString()}")
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