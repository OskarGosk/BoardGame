package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.ApiFirebaseData
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class BoardGameFirebaseDataRepositoryImpl(
    private val apiFirebaseData: ApiFirebaseData
) : BoardGameFirebaseDataRepository {

    override suspend fun getAllGame(): RequestResult<List<Game>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                apiFirebaseData.getAllGame().map { element ->
                    element.value.copy(id = element.key)
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't get All game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun addAllGame(game: Map<String, Game>): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiFirebaseData.addAllGame(game)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't add game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllPlayer(): RequestResult<List<Player>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                apiFirebaseData.getAllPlayer()?.map { element ->
                    element.value.copy(id = element.key)
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't get All player\n  ${it.stackTraceToString()}")
            }
        }.fold(
            onSuccess = { RequestResult.Success(it ?: emptyList()) },
            onFailure = { RequestResult.Error(it) })
    }

    override suspend fun addPlayer(player: Map<String, Player>): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiFirebaseData.addPlayer(player)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllHistoryGame(): RequestResult<List<HistoryGameFirebase>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                apiFirebaseData.getAllHistoryGame().map { element ->
                    element.value.copy(id = element.key)
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't get All History Game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun addHistoryGame(historyGame: Map<String, HistoryGameFirebase>): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiFirebaseData.addHistory(historyGame)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't add History Game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getAllHistoryGameExpansion(): RequestResult<List<HistoryGameExpansion>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                apiFirebaseData.getAllHistoryGameExpansion().map { element ->
                    element.value.copy(id = element.key)
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't get All History Game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun addHistoryGameExpansion(historyGameExpansion: Map<String, HistoryGameExpansion>): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiFirebaseData.addHistoryExpansion(historyGameExpansion)
                when {
                    response.isSuccessful -> {
                        RequestResult.Success(true)
                    }

                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Firebase DB").e("Can't add History Game\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { it }, onFailure = { RequestResult.Error(it) })
    }
}