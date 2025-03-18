package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.models.BoardGames
import com.goskar.boardgame.data.models.SearchList

interface BoardGameApiRepository {

    suspend fun getGame(): BoardGames?

    suspend fun searchGame(name: String): SearchList?

}