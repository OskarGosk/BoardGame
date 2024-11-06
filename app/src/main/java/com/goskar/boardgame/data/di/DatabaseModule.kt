package com.goskar.boardgame.data.di


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goskar.boardgame.data.db.Converters
import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.data.rest.models.Player
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.databaseModule() = module {
    single<Db> {
        Room.databaseBuilder(
            get(),
            Db::class.java, "database-name"
        ).fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }
    single { get<Db>().playerDao() }
    single { get<Db>().gameDao() }
    single { get<Db>().historyGameDao() }

}

@Database(entities = [Player::class, Game::class, HistoryGame::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)

abstract class Db: RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyGameDao(): HistoryGameDao
}