package com.goskar.boardgame.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun ComposeApp() {
    BoardGameTheme {
        Navigator(screen = HomeScreen())
    }
}