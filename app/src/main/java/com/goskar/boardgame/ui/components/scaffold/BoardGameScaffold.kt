package com.goskar.boardgame.ui.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.components.scaffold.bottomBar.BottomBarElements
import com.goskar.boardgame.ui.components.scaffold.bottomBar.BottomNavigation
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBar
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.utils.Keyboard
import com.goskar.boardgame.utils.keyboardAsState

@Composable
fun BoardGameScaffold(
    modifier: Modifier = Modifier,
    titlePage: String,
    showBottomBar: Boolean = true,
    selectedScreen: Int?,
    topBarState: TopBarState = TopBarState(),
    showSynchronizedIcon: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarHost: @Composable () -> Unit = { SnackbarHost(snackbarHostState) },
    uploadDataToFirebase: () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val keyboardState by keyboardAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeGestures,
        modifier = modifier,
        floatingActionButton = floatingActionButton,
        topBar = { TopBar(titlePage, showSynchronizedIcon, topBarState, uploadDataToFirebase) },
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
        },
        snackbarHost = {},
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            content(paddingValues)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                snackbarHost()
            }
        }
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
fun BoardGameScaffoldSnackbarPreview() {
    BoardGameTheme {
        BoardGameScaffold(
            titlePage = "Home",
            selectedScreen = BottomBarElements.HomeButton.title,
            snackbarHost = { Snackbar { Text("Game saved") } },
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


