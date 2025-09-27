package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ApiFirebaseData {

    // GAME
    @PUT("game/UID.json")
    suspend fun addAllGame(@Body game: Map<String, Game>): Response<Void>

    @GET("game/UID.json")
    suspend fun getAllGame(): Map<String, Game>

    // PLAYER
    @PUT("player/UID.json")
    suspend fun addPlayer(@Body player: Map<String,Player>): Response<Void>

    @GET("player/UID.json")
    suspend fun getAllPlayer(): Map<String, Player>?

    // HISTORY
    @PUT("historyGame/UID.json")
    suspend fun addHistory(@Body historyGame: Map<String, HistoryGameFirebase>): Response<Void>

    @GET("historyGame/UID.json")
    suspend fun getAllHistoryGame(): Map<String, HistoryGameFirebase>

    // HISTORY EXPANSION
    @PUT("historyGameExpansion/UID.json")
    suspend fun addHistoryExpansion(@Body historyGame: Map<String, HistoryGameExpansion>): Response<Void>

    @GET("historyGameExpansion/UID.json")
    suspend fun getAllHistoryGameExpansion(): Map<String, HistoryGameExpansion>
}