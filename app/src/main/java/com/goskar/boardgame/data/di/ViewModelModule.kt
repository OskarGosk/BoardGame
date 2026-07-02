package com.goskar.boardgame.data.di

import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gameDetailsBGG.GameDetailsBGGViewModel
import com.goskar.boardgame.ui.gameReports.GameReportsViewModel
import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameViewModel
import com.goskar.boardgame.ui.gamesList.play.GamePlayViewModel
import com.goskar.boardgame.ui.gamesList.newAddGame.AddGameNewViewModel
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.gamesHistory.newAddGameplay.AddGameplayNewViewModel
import com.goskar.boardgame.ui.gamesHistory.newHistory.HistoryNewViewModel
import com.goskar.boardgame.ui.gamesHistory.newSessionDetails.SessionDetailsNewViewModel
import com.goskar.boardgame.ui.home.HomeScreenViewModel
import com.goskar.boardgame.ui.home.newHome.HomeNewViewModel
import com.goskar.boardgame.ui.playerList.PlayerListViewModel
import com.goskar.boardgame.ui.playerList.newAddPlayer.AddPlayerNewViewModel
import com.goskar.boardgame.ui.playerList.newPlayerList.PlayerListNewViewModel
import com.goskar.boardgame.ui.profile.newProfile.ProfileNewViewModel
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
    viewModel { GamesHistoryViewModel(get(), get()) }
    viewModel { HomeScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { HomeNewViewModel() }
    viewModel { AddPlayerNewViewModel() }
    viewModel { AddGameNewViewModel() }
    viewModel { AddGameplayNewViewModel() }
    viewModel { PlayerListNewViewModel() }
    viewModel { HistoryNewViewModel() }
    viewModel { ProfileNewViewModel() }
    viewModel { SessionDetailsNewViewModel() }
    viewModel { GameSearchViewModel(get()) }
    viewModel { GameDetailsBGGViewModel(get(), get(), get()) }
    viewModel { GameReportsViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { TopBarViewModel(get(), get(), get(), get(), get()) }
}