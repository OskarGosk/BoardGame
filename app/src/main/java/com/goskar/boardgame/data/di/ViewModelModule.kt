package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.games.GameListViewModel
import com.goskar.boardgame.ui.player.PlayerListViewModel
import com.goskar.boardgame.ui.player.addEditPlayer.AddEditPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { AddEditPlayerViewModel(get()) }
    viewModel { GameListViewModel(get()) }
}