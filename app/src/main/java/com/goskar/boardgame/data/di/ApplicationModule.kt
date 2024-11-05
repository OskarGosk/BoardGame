package com.goskar.boardgame.data.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication

fun KoinApplication.applicationModule(context: Context) {
    androidLogger()
    androidContext(context)
}