package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.SearchBGGList
import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class BoardGameApiRepositoryImpl(
    private val apiBoardGame: ApiBoardGame,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BoardGameApiRepository {

    companion object {
        val TAG = "BGG API"
    }

    override suspend fun getGame(gameId: String): RequestResult<BoardGamesDetails> {
        return withContext(defaultDispatcher) {
            runCatching {
                apiBoardGame.getBoardGameInfo(gameId)
            }.onFailure {
                Timber.tag(TAG).e("Error BGG API (game details): ${it.stackTraceToString()}")
            }
        }.fold(
            onSuccess = { RequestResult.Success(it) },
            onFailure = { RequestResult.Error(it) }
        )
    }

    override suspend fun searchGame(name: String): RequestResult<SearchBGGList> {
        return withContext(defaultDispatcher) {
            runCatching {
                apiBoardGame.searchGame(name)
            }.onFailure {
                Timber.tag(TAG).e("Error BGG API (search game): ${it.stackTraceToString()}")
            }
        }.fold(
            onSuccess = { RequestResult.Success(it) },
            onFailure = { RequestResult.Error(it) }
        )
    }

}