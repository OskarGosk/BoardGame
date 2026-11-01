package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiFirebaseData {

    @GET("users/{uid}/game.json")
    suspend fun getAllGame(@Path("uid") uid: String): Map<String, Game>?

    @GET("users/{uid}/player.json")
    suspend fun getAllPlayer(@Path("uid") uid: String): Map<String, Player>?

    @GET("users/{uid}/historyGame.json")
    suspend fun getAllHistoryGame(@Path("uid") uid: String): Map<String, HistoryGameFirebase>?

    @GET("users/{uid}/historyGameExpansion.json")
    suspend fun getAllHistoryGameExpansion(@Path("uid") uid: String): Map<String, HistoryGameExpansion>?

    /** PATCH replaces only the collections in the body, all in one write. */
    @PATCH("users/{uid}.json")
    suspend fun updateUserData(@Path("uid") uid: String, @Body data: CloudUserData): Response<Void>

    @GET("users/{uid}.json?shallow=true")
    suspend fun getUserDataKeys(@Path("uid") uid: String): Map<String, Boolean>?

    // Layout used before 2.0.3, read only to move existing accounts to users/{uid}.
    @GET("game/{uid}.json")
    suspend fun getLegacyGames(@Path("uid") uid: String): Map<String, Game>?

    @GET("player/{uid}.json")
    suspend fun getLegacyPlayers(@Path("uid") uid: String): Map<String, Player>?

    @GET("historyGame/{uid}.json")
    suspend fun getLegacyHistoryGames(@Path("uid") uid: String): Map<String, HistoryGameFirebase>?

    @GET("historyGameExpansion/{uid}.json")
    suspend fun getLegacyHistoryGameExpansions(@Path("uid") uid: String): Map<String, HistoryGameExpansion>?
}
