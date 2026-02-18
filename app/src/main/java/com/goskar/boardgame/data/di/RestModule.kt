package com.goskar.boardgame.data.di

import com.google.gson.Gson
import com.goskar.boardgame.BuildConfig
import com.goskar.boardgame.Constants.API_URL_BOARD
import com.goskar.boardgame.Constants.BGG_CLIENT
import com.goskar.boardgame.Constants.BGG_RETROFIT
import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.AuthBggInterceptor
import okhttp3.OkHttpClient
import org.koin.core.KoinApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

fun KoinApplication.restModule() = module {
    single(BGG_CLIENT) {
        OkHttpClient.Builder()
            .addInterceptor(AuthBggInterceptor(BuildConfig.BGG_API_KEY))
            .build()
    }
    single {
        Gson()
    }
    single(BGG_RETROFIT) {
        Retrofit.Builder()
            .client(get(BGG_CLIENT))
            .baseUrl(API_URL_BOARD)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<ApiBoardGame> {
        get<Retrofit>(BGG_RETROFIT).create(ApiBoardGame::class.java)
    }
}
