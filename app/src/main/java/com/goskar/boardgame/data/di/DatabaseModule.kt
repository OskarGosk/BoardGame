package com.goskar.boardgame.data.di

import OGosk.boardgamebase.database.Converters
import OGosk.boardgamebase.database.GameDao
import OGosk.boardgamebase.database.HistoryGameDao
import OGosk.boardgamebase.database.PlayerDao
import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.Player
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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