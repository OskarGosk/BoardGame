package com.goskar.boardgame.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat
import com.goskar.boardgame.ui.ComposeApp
import com.goskar.boardgame.ui.theme.BoardGameTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightNavigationBars = true
                isAppearanceLightStatusBars = true
            }
            BoardGameTheme {
                ComposeApp()
            }
        }
    }

}