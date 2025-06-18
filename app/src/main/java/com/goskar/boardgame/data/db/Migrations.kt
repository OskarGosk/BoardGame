package com.goskar.boardgame.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User

@Database(
    entities = [Player::class, Game::class, HistoryGame::class, User::class],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
    ]
)
@TypeConverters(Converters::class)

abstract class Db : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyGameDao(): HistoryGameDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Game ADD COLUMN uriFromBgg TEXT default(NULL)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Game ADD COLUMN baseGameId TEXT default(NULL)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS User (\n" +
                        "                id INTEGER NOT NULL default (0),\n" +
                        "                email TEXT default(NULL),\n" +
                        "                token TEXT default(NULL),\n" +
                        "                PRIMARY KEY(id)\n" +
                        "            )")
            }
        }
    }

//    @RenameColumn (tableName = "Game", fromColumnName = "created", toColumnName = "createdAt") // Manual Migration
//    class Migration3To4: AutoMigrationSpec
}

