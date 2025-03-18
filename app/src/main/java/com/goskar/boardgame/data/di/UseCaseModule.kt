package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import org.koin.core.KoinApplication
import org.koin.dsl.module


fun KoinApplication.useCaseModule() = module {
    single { GetAllGameUseCase(get()) }
}