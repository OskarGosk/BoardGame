package com.goskar.boardgame.DoPrzeniesienia

import OGosk.boardgamebase.api.GameNetworkRepository
import OGosk.boardgamebase.api.HistoryGameNetworkRepository
import OGosk.boardgamebase.api.PlayerNetworkRepository
import OGosk.boardgamebase.api.ServiceConfiguration
import OGosk.boardgamebase.database.DatabaseConfiguration
import OGosk.boardgamebase.database.GameDatabaseRepository
import OGosk.boardgamebase.database.HistoryGameDatabaseRepository
import OGosk.boardgamebase.database.PlayerDatabaseRepository
import OGosk.boardgamebase.viewModel.GameViewModel
import OGosk.boardgamebase.viewModel.HistoryGameViewModel
import OGosk.boardgamebase.viewModel.PlayerViewModel
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.scope.get
import org.koin.dsl.module

class MainActivity : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainActivity)
            modules(
                module {
                    single { DatabaseConfiguration.getDatabase(androidContext()) }
                    factory { PlayerDatabaseRepository(get()) }
//                    single { DatabaseConfiguration2.getDatabase(androidContext()) }
                    factory { GameDatabaseRepository(get()) }
                    factory { HistoryGameDatabaseRepository(get()) }

                    single { ServiceConfiguration.gameService }
                    factory { GameNetworkRepository(get()) }

                    single { ServiceConfiguration.playerService }
                    factory { PlayerNetworkRepository(get()) }

                    single { ServiceConfiguration.historyGameService }
                    factory { HistoryGameNetworkRepository(get()) }

                    viewModel { PlayerViewModel(get(), get()) }
                    viewModel { GameViewModel(get(), get()) }
                    viewModel { HistoryGameViewModel(get(), get()) }

                }
            )
        }
    }
}