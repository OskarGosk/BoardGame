package com.goskar.boardgame.data.di


import androidx.room.Room
import com.goskar.boardgame.Constants.DATABASE_NAME
import com.goskar.boardgame.data.db.Db
import com.goskar.boardgame.data.db.Db.Companion.MIGRATION_3_4
import com.goskar.boardgame.data.db.Db.Companion.MIGRATION_4_5
import com.goskar.boardgame.data.db.Db.Companion.MIGRATION_5_6
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.databaseModule() = module {
    single<Db> {
        Room.databaseBuilder(
            get(),
            Db::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigrationOnDowngrade()
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .build()
    }
    single { get<Db>().playerDao() }
    single { get<Db>().gameDao() }
    single { get<Db>().historyGameDao() }
    single { get<Db>().userSessionDao() }

}