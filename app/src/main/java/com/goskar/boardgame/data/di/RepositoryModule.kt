package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.repository.GameNetworkRepositoryImpl
import com.goskar.boardgame.data.repository.HistoryGameNetworkRepository
import com.goskar.boardgame.data.repository.HistoryGameNetworkRepositoryImpl
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import com.goskar.boardgame.data.repository.PlayerNetworkRepositoryImpl
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.repositoryModule() = module {
    single<PlayerNetworkRepository> { PlayerNetworkRepositoryImpl(get())}
    single<GameNetworkRepository> { GameNetworkRepositoryImpl(get())}
    single<HistoryGameNetworkRepository> { HistoryGameNetworkRepositoryImpl(get())}

}