package com.goskar.boardgame.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.ui.gamesHistory.newAddGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.gamesList.MyGamesCollectionScreen
import com.goskar.boardgame.ui.home.newHome.HomeNewScreen
import com.goskar.boardgame.ui.playerList.newPlayerList.PlayerListNewScreen

/**
 * A single bottom-navigation destination: label + icon(s) + the screen it opens.
 */
data class BgNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val navigationScreen: Screen,
)

/**
 * Canonical bottom-nav tabs for the redesigned app — the single source of truth.
 * Order matters: it maps 1:1 to the tab index used by [AppScaffold] / the bottom bar.
 */
val appNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home, navigationScreen = HomeNewScreen()),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List, navigationScreen = MyGamesCollectionScreen()),
    BgNavItem("Add Session", Icons.Default.AddCircle, navigationScreen = AddGameplayNewScreen()),
    BgNavItem("Players", Icons.Default.Person, navigationScreen = PlayerListNewScreen()),
)

/** Alias kept for component previews. */
val previewNavItems = appNavItems
