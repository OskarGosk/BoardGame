package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.goskar.boardgame.data.models.HistoryGameExpansion

@Dao
interface HistoryGameExpansionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpansion(expansion: HistoryGameExpansion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpansion(historyExpansionList: List<HistoryGameExpansion>)

    @Query("DELETE FROM HistoryGameExpansion")
    suspend fun deleteAll()

    @Upsert
    suspend fun insertAllExpansionFromFirebase(historyExpansionList: List<HistoryGameExpansion>)

    @Query("SELECT * FROM historygameexpansion")
    suspend fun getAll(): List<HistoryGameExpansion>
}