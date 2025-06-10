package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.GetAllPlayerUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.useCaseModule() = module {
    single { GetAllGameUseCase(get()) }
    single { GetAllPlayerUseCase(get()) }
    single { GetAllHistoryGameUseCase(get()) }
    single { UpsertAllGameUseCase(get()) }
    single { UpsertAllPlayerUseCase(get()) }
    single { UpsertAllHistoryGameUseCase(get()) }
    single { ClearDbUseCase(get(), get(), get()) }
}