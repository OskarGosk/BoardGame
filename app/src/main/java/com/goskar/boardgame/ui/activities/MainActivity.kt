package com.goskar.boardgame.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.goskar.boardgame.ui.ComposeApp
import com.goskar.boardgame.ui.theme.BoardGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGameTheme {
                ComposeApp()
            }
        }
    }
}