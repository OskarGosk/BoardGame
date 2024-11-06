package com.goskar.boardgame.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.data.rest.models.Player

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

@Dao
interface GameDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
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

@Dao
interface HistoryGameDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
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