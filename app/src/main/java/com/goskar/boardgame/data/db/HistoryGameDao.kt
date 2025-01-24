package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.goskar.boardgame.data.models.HistoryGame

@Dao
interface HistoryGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyGame: HistoryGame)

    @Upsert
    suspend fun insertAll(historyGameList: List<HistoryGame>)

    @Query("SELECT * FROM historyGame")
    suspend fun getAll():List<HistoryGame>

    @Delete
    suspend fun delete(historyGame: HistoryGame)

    @Update
    suspend fun edit(historyGame: HistoryGame)
}