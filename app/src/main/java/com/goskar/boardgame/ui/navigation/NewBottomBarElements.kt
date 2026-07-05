package com.goskar.boardgame.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.collection.MyGamesCollectionScreen
import com.goskar.boardgame.ui.screens.home.HomeNewScreen
import com.goskar.boardgame.ui.screens.players.PlayerListNewScreen

data class BgNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val navigationScreen: Screen,
)

val appNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home, navigationScreen = HomeNewScreen()),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List, navigationScreen = MyGamesCollectionScreen()),
    BgNavItem("Add Session", Icons.Default.AddCircle, navigationScreen = AddGameplayNewScreen()),
    BgNavItem("Players", Icons.Default.Person, navigationScreen = PlayerListNewScreen()),
)

val previewNavItems = appNavItems
