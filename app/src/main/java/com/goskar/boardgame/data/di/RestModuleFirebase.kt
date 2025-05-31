package com.goskar.boardgame.data.di

import com.google.gson.Gson
import com.goskar.boardgame.Constants.API_URL
import com.goskar.boardgame.Constants.FIREBASE_RETROFIT
import com.goskar.boardgame.data.rest.ApiFirebaseData
import com.goskar.boardgame.data.rest.AuthInterceptor
import com.goskar.boardgame.data.rest.UIDInterceptor
import okhttp3.OkHttpClient
import org.koin.core.KoinApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun KoinApplication.restModuleFirebase() = module {
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(UIDInterceptor(get()))
            .addInterceptor(AuthInterceptor(get()))
            .build()
    }
    single {
        Gson()
    }
    single(FIREBASE_RETROFIT) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<ApiFirebaseData> {
        get<Retrofit>(FIREBASE_RETROFIT).create(ApiFirebaseData::class.java)
    }
}