package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.gameReports.GameReportsViewModel
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameListViewModel
import com.goskar.boardgame.ui.screens.addGame.viewmodel.AddGameViewModel
import com.goskar.boardgame.ui.screens.history.viewmodel.GamesHistoryViewModel
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.AddGameplayViewModel
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.SessionDetailsViewModel
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsViewModel
import com.goskar.boardgame.ui.components.user.CurrentUserViewModel
import com.goskar.boardgame.ui.screens.home.HomeViewModel
import com.goskar.boardgame.ui.screens.players.PlayerListViewModel
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerViewModel
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileViewModel
import com.goskar.boardgame.ui.screens.login.LoginViewModel
import com.goskar.boardgame.ui.screens.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { GameListViewModel(get()) }
    viewModel { GamesHistoryViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddPlayerViewModel(get()) }
    viewModel { AddGameViewModel(get(), get(), get()) }
    viewModel { AddGameplayViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SessionDetailsViewModel(get(), get()) }
    viewModel { GameDetailsViewModel(get(), get()) }
    viewModel { CurrentUserViewModel(get(), get(), get()) }
    viewModel { GameReportsViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
}