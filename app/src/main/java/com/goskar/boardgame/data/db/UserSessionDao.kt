package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.goskar.boardgame.data.models.User

@Dao
interface UserSessionDao {

    @Upsert
    suspend fun insert(user: User)

    @Upsert
    suspend fun logout(user: User)

    @Query ("SELECT * FROM User WHERE id = 0")
    suspend fun current(): User?

    @Query("SELECT token FROM User WHERE id = 0")
    fun getToken (): String?

    @Query("SELECT userUID FROM User WHERE id = 0")
    fun getUserUID (): String?
}