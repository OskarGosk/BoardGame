package com.goskar.boardgame.data.di


import androidx.room.Room
import com.goskar.boardgame.Constants.DATABASE_NAME
import com.goskar.boardgame.data.db.Db
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.databaseModule() = module {
    single<Db> {
        Room.databaseBuilder(
            get(),
            Db::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }
    single { get<Db>().playerDao() }
    single { get<Db>().gameDao() }
    single { get<Db>().historyGameDao() }

}