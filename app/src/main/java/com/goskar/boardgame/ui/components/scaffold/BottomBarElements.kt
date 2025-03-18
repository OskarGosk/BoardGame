package com.goskar.boardgame.ui.components.scaffold

import androidx.annotation.StringRes
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gamesHistory.HistoryGameListScreen
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.playerList.PlayerListScreen

enum class BottomBarElements (
    @StringRes var title: Int,
    var navigationScreen: Screen?
) {
    HomeButton(R.string.home, HomeScreen()),
    PlayerListButton(R.string.player_list, PlayerListScreen()),
    GameListButton(R.string.board_list, GameListScreen()),
    HistoryListButton(R.string.history_game_screen, HistoryGameListScreen()),
}