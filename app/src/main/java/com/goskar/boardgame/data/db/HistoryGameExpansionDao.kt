package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goskar.boardgame.data.models.HistoryGameExpansion

@Dao
interface HistoryGameExpansionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpansion(expansion: HistoryGameExpansion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpansion(historyExpansionList: List<HistoryGameExpansion>)

    @Query("DELETE FROM HistoryGameExpansion")
    suspend fun deleteAll()

    @Query("DELETE FROM HistoryGameExpansion WHERE historyGameId = :historyGameId")
    suspend fun deleteByHistoryGameId(historyGameId: String)

    @Query("SELECT * FROM historygameexpansion")
    suspend fun getAll(): List<HistoryGameExpansion>
}