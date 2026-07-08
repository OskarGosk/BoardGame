package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gameDetailsBGG.GameDetailsBGGViewModel
import com.goskar.boardgame.ui.gameReports.GameReportsViewModel
import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameViewModel
import com.goskar.boardgame.ui.gamesList.play.GamePlayViewModel
import com.goskar.boardgame.ui.screens.addGame.viewmodel.AddGameNewViewModel
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.AddGameplayNewViewModel
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.SessionDetailsNewViewModel
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewViewModel
import com.goskar.boardgame.ui.components.user.CurrentUserViewModel
import com.goskar.boardgame.ui.screens.home.viewmodel.HomeNewViewModel
import com.goskar.boardgame.ui.screens.players.PlayerListViewModel
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerNewViewModel
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewViewModel
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchViewModel
import com.goskar.boardgame.ui.login.LoginViewModel
import com.goskar.boardgame.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.viewModelModule() = module {
    viewModel { PlayerListViewModel(get()) }
    viewModel { GameListViewModel(get()) }
    viewModel { AddEditGameViewModel(get(), get()) }
    viewModel { GamePlayViewModel(get(), get(), get(), get()) }
    viewModel { GamesHistoryViewModel(get(), get(), get()) }
    viewModel { HomeNewViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddPlayerNewViewModel(get()) }
    viewModel { AddGameNewViewModel(get(), get(), get()) }
    viewModel { AddGameplayNewViewModel(get(), get(), get(), get()) }
    viewModel { ProfileNewViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SessionDetailsNewViewModel(get(), get()) }
    viewModel { GameDetailsNewViewModel(get(), get()) }
    viewModel { CurrentUserViewModel(get(), get(), get()) }
    viewModel { GameSearchViewModel(get()) }
    viewModel { GameDetailsBGGViewModel(get(), get(), get()) }
    viewModel { GameReportsViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { TopBarViewModel(get(), get(), get(), get(), get()) }
}