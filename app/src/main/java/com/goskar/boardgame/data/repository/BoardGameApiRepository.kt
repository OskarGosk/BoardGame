package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.models.BoardGameBGG
import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.SearchBGGList
import com.goskar.boardgame.data.rest.RequestResult

interface BoardGameApiRepository {

    suspend fun getGame(gameID: String): RequestResult<BoardGamesDetails>

    suspend fun searchGame(name: String): RequestResult<SearchBGGList>

}