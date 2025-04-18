package com.goskar.boardgame.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player

@Database(
    entities = [Player::class, Game::class, HistoryGame::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
//        AutoMigration(from = 3, to = 4, spec = Db.Migration3To4::class) // Manual Migration
    ]
)
@TypeConverters(Converters::class)

abstract class Db : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyGameDao(): HistoryGameDao

//    @RenameColumn (tableName = "Game", fromColumnName = "created", toColumnName = "createdAt") // Manual Migration
//    class Migration3To4: AutoMigrationSpec
}

