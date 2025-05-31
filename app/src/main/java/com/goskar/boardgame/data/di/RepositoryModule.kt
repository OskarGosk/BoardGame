package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepositoryImpl
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepositoryImpl
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepositoryImpl
import com.goskar.boardgame.data.repository.bbg.BoardGameApiRepository
import com.goskar.boardgame.data.repository.bbg.BoardGameApiRepositoryImpl
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepositoryImpl
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.repository.user.UserRepositoryImpl
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.repositoryModule() = module {
    single<PlayerDbRepository> { PlayerDbRepositoryImpl(get()) }
    single<GameDbRepository> { GameDbRepositoryImpl(get()) }
    single<GamesHistoryDbRepository> { GamesHistoryDbRepositoryImpl(get()) }
    single<BoardGameApiRepository> { BoardGameApiRepositoryImpl(get()) }
    single<BoardGameFirebaseDataRepository> { BoardGameFirebaseDataRepositoryImpl(get()) }
    single<UserRepository> {UserRepositoryImpl(get())}
}