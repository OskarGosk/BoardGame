package com.goskar.boardgame.ui.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.utils.keyboardAsState

@Composable
fun BoardGameScaffold(
    modifier: Modifier = Modifier,
    titlePage: String,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.then(
            Modifier
                .systemBarsPadding()
                .imePadding()
        ),
        topBar = { TopBar(titlePage) },
        bottomBar = {
            Box {}
        }

    ) {
        content(it)
    }
}

@Preview
@Composable
fun BoardGameScaffoldPreview() {
    BoardGameTheme {
        BoardGameScaffold (
            titlePage = "Oskar"
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
    BoardGameTheme (darkTheme = true) {
        BoardGameScaffold (
            titlePage = "Cimeny"
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


