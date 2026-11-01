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

/**
 * Reads return [Response] because Firebase answers `null` for a missing node and Retrofit rejects a
 * null body for a plain suspend return type, even a nullable one.
 */
interface ApiFirebaseData {

    @GET("users/{uid}.json")
    suspend fun getUserData(@Path("uid") uid: String): Response<CloudUserData>

    /** PATCH replaces only the collections in the body, all in one write. */
    @PATCH("users/{uid}.json")
    suspend fun updateUserData(@Path("uid") uid: String, @Body data: CloudUserData): Response<Void>

    @GET("users/{uid}.json?shallow=true")
    suspend fun getUserDataKeys(@Path("uid") uid: String): Response<Map<String, Boolean>>

    // Layout used before 2.0.3, read only to move existing accounts to users/{uid}.
    @GET("game/{uid}.json")
    suspend fun getLegacyGames(@Path("uid") uid: String): Response<Map<String, Game>>

    @GET("player/{uid}.json")
    suspend fun getLegacyPlayers(@Path("uid") uid: String): Response<Map<String, Player>>

    @GET("historyGame/{uid}.json")
    suspend fun getLegacyHistoryGames(@Path("uid") uid: String): Response<Map<String, HistoryGameFirebase>>

    @GET("historyGameExpansion/{uid}.json")
    suspend fun getLegacyHistoryGameExpansions(@Path("uid") uid: String): Response<Map<String, HistoryGameExpansion>>
}
