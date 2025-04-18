package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import com.goskar.boardgame.data.oflineRepository.GameDbRepositoryImpl
import com.goskar.boardgame.data.oflineRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.oflineRepository.GamesHistoryDbRepositoryImpl
import com.goskar.boardgame.data.oflineRepository.PlayerDbRepository
import com.goskar.boardgame.data.oflineRepository.PlayerDbRepositoryImpl
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import com.goskar.boardgame.data.repository.BoardGameApiRepositoryImpl
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.repositoryModule() = module {
    single<PlayerDbRepository> { PlayerDbRepositoryImpl(get()) }
    single<GameDbRepository> { GameDbRepositoryImpl(get()) }
    single<GamesHistoryDbRepository> { GamesHistoryDbRepositoryImpl(get()) }
    single<BoardGameApiRepository> {BoardGameApiRepositoryImpl(get())}
}