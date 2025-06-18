package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.SearchBGGList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiBoardGame {

    @GET("search")
    suspend fun searchGame(
        @Query("search") searchName: String,
    ): SearchBGGList

    @GET("game/{id}")
    suspend fun getBoardGameInfo(
        @Path("id") gameID: String,
        ): BoardGamesDetails
}