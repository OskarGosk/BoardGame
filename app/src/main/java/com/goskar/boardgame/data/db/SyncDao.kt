package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.Player

@Dao
abstract class SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDeleted(record: DeletedRecord)

    @Query("SELECT * FROM DeletedRecord")
    abstract suspend fun getAllDeleted(): List<DeletedRecord>

    @Query("DELETE FROM DeletedRecord")
    abstract suspend fun deleteAllDeleted()

    /** Swaps the whole local library for the merged one in one transaction, so a failure leaves it untouched. */
    @Transaction
    open suspend fun replaceAll(
        games: List<Game>,
        players: List<Player>,
        history: List<HistoryGame>,
        expansions: List<HistoryGameExpansion>,
        deleted: List<DeletedRecord>,
    ) {
        clearGames()
        clearPlayers()
        clearHistory()
        clearExpansions()
        deleteAllDeleted()
        insertGames(games)
        insertPlayers(players)
        insertHistory(history)
        insertExpansions(expansions)
        insertAllDeleted(deleted)
    }

    @Query("DELETE FROM Game")
    protected abstract suspend fun clearGames()

    @Query("DELETE FROM Player")
    protected abstract suspend fun clearPlayers()

    @Query("DELETE FROM HistoryGame")
    protected abstract suspend fun clearHistory()

    @Query("DELETE FROM HistoryGameExpansion")
    protected abstract suspend fun clearExpansions()

    @Insert
    protected abstract suspend fun insertGames(games: List<Game>)

    @Insert
    protected abstract suspend fun insertPlayers(players: List<Player>)

    @Insert
    protected abstract suspend fun insertHistory(history: List<HistoryGame>)

    @Insert
    protected abstract suspend fun insertExpansions(expansions: List<HistoryGameExpansion>)

    @Insert
    protected abstract suspend fun insertAllDeleted(deleted: List<DeletedRecord>)
}
