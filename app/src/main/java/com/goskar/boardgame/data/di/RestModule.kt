package com.goskar.boardgame.data.di

import com.google.gson.Gson
import com.goskar.boardgame.data.rest.ApiBoardGame
import okhttp3.OkHttpClient
import org.koin.core.KoinApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun KoinApplication.restModule() = module {
    single<OkHttpClient> {
        OkHttpClient.Builder().build()
    }
    single {
        Gson()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://board-game-goskar-default-rtdb.europe-west1.firebasedatabase.app/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<ApiBoardGame> {
        get<Retrofit>().create(ApiBoardGame::class.java)
    }
}