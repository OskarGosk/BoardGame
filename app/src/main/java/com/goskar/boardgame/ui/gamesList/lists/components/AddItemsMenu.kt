package com.goskar.boardgame.ui.gamesList.lists.components

import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.utils.FloatingMenuList

enum class AddItemsMenu(val items : FloatingMenuList) {
    MANUAL(FloatingMenuList(R.drawable.icons_search, null, AddEditGameScreen(null))),
    BGG(FloatingMenuList(null, R.string.bgg, GameSearchScreen())),
}