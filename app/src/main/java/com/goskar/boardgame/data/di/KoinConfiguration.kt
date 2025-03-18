package com.goskar.boardgame.data.di

import android.content.Context
import org.koin.core.context.GlobalContext.startKoin


fun Context.configureKoin(inPreview: Boolean = false) {
    startKoin {
        applicationModule(this@configureKoin)
        modules(
            restModule(),
            databaseModule(),
            coilModule(),
            repositoryModule(),
            viewModelModule(),
            useCaseModule()
        )
    }
}
