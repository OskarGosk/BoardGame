package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.gameDetailsBGG.GameDetailsBGGViewModel
import com.goskar.boardgame.ui.gameRaports.GameReportsViewModel
import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameViewModel
import com.goskar.boardgame.ui.gamesList.play.GamePlayViewModel
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.home.HomeScreenViewModel
import com.goskar.boardgame.ui.playerList.PlayerListViewModel
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { GameListViewModel(get(), get()) }
    viewModel { AddEditGameViewModel(get(), get()) }
    viewModel { GamePlayViewModel(get(), get(), get()) }
    viewModel { GamesHistoryViewModel(get()) }
    viewModel { HomeScreenViewModel(get()) }
    viewModel { GameSearchViewModel(get()) }
    viewModel { GameDetailsBGGViewModel(get(), get(), get()) }
    viewModel { GameReportsViewModel(get(), get()) }
}