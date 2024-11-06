package com.goskar.boardgame.data.rest

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.GameIdRespons
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse
import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.model.PlayerIdRespons
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiBoardGame {

    @POST("game.json")
    suspend fun addGame(@Body game: Game): GameIdRespons

    @GET("game.json")
    suspend fun getAllGame(): Map<String, Game>

    @DELETE("game/{id}.json")
    suspend fun deleteGame(@Path("id") gameId: String)

    @PUT("game/{id}.json")
    suspend fun editGame(
        @Path("id") gameId: String,
        @Body game: Game
    )

    @POST("player.json")
    suspend fun addPlayer(@Body player: Player): PlayerIdRespons

    @GET("player.json")
    suspend fun getAllPlayer(): Map<String, Player>

    @DELETE("player/{id}.json")
    suspend fun deletePlayer(@Path("id") playerId: String)

    @PUT("player/{id}.json")
    suspend fun editPlayer(
        @Path("id") playerId: String,
        @Body player: Player
    )

    @POST("historyGame.json")
    suspend fun addHistory(@Body historyGame: HistoryGame): HistoryIdResponse

    @GET("historyGame.json")
    suspend fun getAllHistoryGame(): Map<String, HistoryGame>

    @DELETE("historyGame/{id}.json")
    suspend fun deleteHistoryGame(@Path("id") historyId: String)

    @PUT("historyGame/{id{.json")
    suspend fun editHistoryGame(
        @Path("id") historyId: String,
        @Body historyGame: HistoryGame
    )
}