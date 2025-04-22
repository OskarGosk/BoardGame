package com.goskar.boardgame.ui.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.utils.Keyboard
import com.goskar.boardgame.utils.keyboardAsState

@Composable
fun BoardGameScaffold(
    modifier: Modifier = Modifier,
    titlePage: String,
    showBottomBar: Boolean = true,
    selectedScreen: Int?,
    content: @Composable (PaddingValues) -> Unit,
) {
    val keyboardState by keyboardAsState()

    Scaffold(
        modifier = modifier.then(
            Modifier
                .systemBarsPadding()
                .imePadding()
        ),
        topBar = { TopBar(titlePage) },
        bottomBar = {
            if (showBottomBar) {
                if (keyboardState == Keyboard.Closed) {
                    BottomNavigation(selectedScreen)
                } else {
                    Box {}
                }
            } else {
                HorizontalDivider(
                    thickness = 20.dp,
                    color = Color.White
                )
            }
        }

    ) {
        content(it)
    }
}

@Preview
@Composable
fun BoardGameScaffoldPreview() {
    BoardGameTheme {
        BoardGameScaffold(
            titlePage = "BottomBarElements.HomeButton.title,",
            selectedScreen = BottomBarElements.HomeButton.title,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Text(text = "Hello")
            }
        }
    }
}

@Preview
@Composable
fun BoardGamePreviewDark() {
    BoardGameTheme(darkTheme = true) {
        BoardGameScaffold(
            titlePage = "BottomBarElements.GameListButton.title",
            selectedScreen = BottomBarElements.GameListButton.title
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Text(text = "Hello")
            }
        }
    }
}


