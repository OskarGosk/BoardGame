package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.models.BoardGames
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.SearchBGGList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiBoardGame {

    @POST("game.json")
    suspend fun addGame(@Body game: Game): Response<Void>

    @GET("game.json")
    suspend fun getAllGame(): Map<String, Game>

    @DELETE("game/{id}.json")
    suspend fun deleteGame(@Path("id") gameId: String): Response<Void>

    @PUT("game/{id}.json")
    suspend fun editGame(
        @Path("id") gameId: String,
        @Body game: Game
    ): Response<Void>

    @POST("player.json")
    suspend fun addPlayer(@Body player: Player): Response<Void>

    @GET("player.json")
    suspend fun getAllPlayer(): Map<String, Player>?

    @DELETE("player/{id}.json")
    suspend fun deletePlayer(@Path("id") playerId: String): Response<Void>

    @PUT("player/{id}.json")
    suspend fun editPlayer(
        @Path("id") playerId: String,
        @Body player: Player
    ): Response<Void>

    @POST("historyGame.json")
    suspend fun addHistory(@Body historyGame: HistoryGame): Response<Void>

    @GET("historyGame.json")
    suspend fun getAllHistoryGame(): Map<String, HistoryGame>

    @DELETE("historyGame/{id}.json")
    suspend fun deleteHistoryGame(@Path("id") historyId: String)

    @PUT("historyGame/{id}.json")
    suspend fun editHistoryGame(
        @Path("id") historyId: String,
        @Body historyGame: HistoryGame
    )

    @GET("search")
    suspend fun searchGame(
        @Query("search") searchName: String,
    ): Response<SearchBGGList>

    @GET("game/222589")
    suspend fun getBoardGameInfo(): Response<BoardGames>
}