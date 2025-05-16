package com.goskar.boardgame.ui.gameRaports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold

class GameReportsScreen : Screen {
    @Composable
    override fun Content() {
        GameReportsContent()
    }
}

@Composable
fun GameReportsContent() {
    BoardGameScaffold(
        titlePage = stringResource(R.string.reports),
        selectedScreen = null
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(text = "Wykres słupkowy z liczbą gier")
            Text(text = "Do wyboru - rok kalendarzowy, miesiąc, tydzień")
        }
    }
}


@Preview
@Composable
fun GameReportsContentPreview() {
    GameReportsContent()
}