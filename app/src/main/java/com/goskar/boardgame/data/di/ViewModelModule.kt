package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameViewModel
import com.goskar.boardgame.ui.gamesList.play.GamePlayViewModel
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.player.playerList.PlayerListViewModel
import com.goskar.boardgame.ui.player.addEditPlayer.AddEditPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { AddEditPlayerViewModel(get(), get()) }
    viewModel { GameListViewModel(get()) }
    viewModel { AddEditGameViewModel(get()) }
    viewModel { GamePlayViewModel(get(), get(), get()) }
    viewModel { GamesHistoryViewModel(get())}
}