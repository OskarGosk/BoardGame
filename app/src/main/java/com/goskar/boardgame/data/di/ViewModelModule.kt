package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.games.lists.GameListViewModel
import com.goskar.boardgame.ui.games.addEditGame.AddEditGameViewModel
import com.goskar.boardgame.ui.games.play.GamePlayViewModel
import com.goskar.boardgame.ui.player.PlayerListViewModel
import com.goskar.boardgame.ui.player.addEditPlayer.AddEditPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { AddEditPlayerViewModel(get()) }
    viewModel { GameListViewModel(get()) }
    viewModel { AddEditGameViewModel(get()) }
    viewModel { GamePlayViewModel(get(), get(), get()) }
}