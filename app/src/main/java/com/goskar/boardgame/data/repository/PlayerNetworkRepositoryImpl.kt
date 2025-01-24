package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlayerNetworkRepositoryImpl(
    private val apiBoardGame: ApiBoardGame,
) : PlayerNetworkRepository {

    override suspend fun addPlayer(player: Player): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.addPlayer(player)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add player").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllPlayer(): List<Player>? {
        return withContext(Dispatchers.IO) {
            runCatching {
                apiBoardGame.getAllPlayer()?.map { element ->
                    element.value.copy(id = element.key)
                }
            }.onFailure {
                Timber.tag("Player").e("Can't get All player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { emptyList() })
    }

    override suspend fun deletePlayer(playerId: String): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.deletePlayer(playerId)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }
                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Player").e("Can't delete player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun editPlayer(player: Player): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.editPlayer(player.id, player)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add player").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }
}