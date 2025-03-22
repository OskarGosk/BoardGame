package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.models.BoardGames
import com.goskar.boardgame.data.models.SearchList
import com.goskar.boardgame.data.rest.ApiBoardGame
import timber.log.Timber

class BoardGameApiRepositoryImpl(private val apiBoardGame: ApiBoardGame) : BoardGameApiRepository {

    override suspend fun getGame(): BoardGames? {
        return runCatching {
            val response = apiBoardGame.getBoardGameInfo()
            if (response.isSuccessful) {
                response.body()
            } else {
                Timber.tag("Oskar22").e("Błąd API: ${response.code()} ${response.message()}")
                null
            }
        }.getOrElse {
            Timber.tag("Oskar22").e("Nie można pobrać gry\n ${it.stackTraceToString()}")
            null
        }
    }

    override suspend fun searchGame(name: String): SearchList? {
        return runCatching {
            val response = apiBoardGame.searchGame(name)
            if (response.isSuccessful) {
                response.body()
            } else {
            Timber.tag("Oskar22").e("Błąd API: ${response.code()} ${response.message()}")
            null
        }
        }.getOrElse {
            Timber.tag("Oskar22").e("Nie można pobrać gry\n ${it.stackTraceToString()}")
            null
        }
    }

}