package com.goskar.boardgame

import android.app.Application
import coil.Coil
import com.goskar.boardgame.data.di.configureKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

class App : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        configureKoin()
        Coil.setImageLoader(factory = get())
        Timber.plant(Timber.DebugTree())
    }
}