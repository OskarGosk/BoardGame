package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.utils.FloatingMenuList

enum class AddItemsMenu(val items : FloatingMenuList) {
    MANUAL(FloatingMenuList(Icons.Filled.Build, null, AddEditGameScreen(null))),
    BGG(FloatingMenuList(null, R.string.bgg, GameSearchScreen())),
}