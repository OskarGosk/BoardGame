package com.goskar.boardgame.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.collection.MyGamesCollectionScreen
import com.goskar.boardgame.ui.screens.home.HomeScreen
import com.goskar.boardgame.ui.screens.players.PlayerListScreen

data class BgNavItem(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val navigationScreen: Screen,
)

val appNavItems = listOf(
    BgNavItem(R.string.nav_home, Icons.Default.Home, navigationScreen = HomeScreen()),
    BgNavItem(R.string.nav_collection, Icons.AutoMirrored.Filled.List, navigationScreen = MyGamesCollectionScreen()),
    BgNavItem(R.string.nav_add_session, Icons.Default.AddCircle, navigationScreen = AddGameplayNewScreen()),
    BgNavItem(R.string.nav_players, Icons.Default.Person, navigationScreen = PlayerListScreen()),
)

val previewNavItems = appNavItems
