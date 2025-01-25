package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.goskar.boardgame.data.models.Player

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: Player)

    @Upsert
    suspend fun insertAll(playerList: List<Player>)

    @Query("SELECT * FROM player")
    suspend fun getAll():List<Player>

    @Delete
    suspend fun delete(player: Player)

    @Update
    suspend fun edit(player: Player)

}