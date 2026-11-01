package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.models.GuestBackupDto
import com.goskar.boardgame.data.models.GuestBackupMeta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Guest backups live under guestBackup/{deviceHash}; the token is passed per call (see GuestBackupRepositoryImpl).
 * Reads return [Response] because Firebase answers `null` when there is no backup and Retrofit rejects a null
 * body for a plain suspend return type, even a nullable one.
 */
interface ApiGuestBackup {

    @PUT("guestBackup/{deviceHash}.json")
    suspend fun upload(
        @Path("deviceHash") deviceHash: String,
        @Query("auth") token: String,
        @Body backup: GuestBackupDto,
    ): Response<Void>

    @GET("guestBackup/{deviceHash}.json")
    suspend fun download(
        @Path("deviceHash") deviceHash: String,
        @Query("auth") token: String,
    ): Response<GuestBackupDto>

    @GET("guestBackup/{deviceHash}/meta.json")
    suspend fun getMeta(
        @Path("deviceHash") deviceHash: String,
        @Query("auth") token: String,
    ): Response<GuestBackupMeta>
}
