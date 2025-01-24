package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.goskar.boardgame.data.models.Game

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)

    @Upsert
    suspend fun insertAll(gameList: List<Game>)

    @Query("SELECT * FROM game")
    suspend fun getAll():List<Game>

    @Delete
    suspend fun delete(game: Game)

    @Update
    suspend fun edit(game: Game)

}