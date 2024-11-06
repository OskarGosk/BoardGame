package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.player.PlayerListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel {PlayerListViewModel(get())}
}