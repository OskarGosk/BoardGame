package com.goskar.boardgame.ui.gameSearchBGG

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements

class GameSearchScreen:Screen {
    @Composable
    override fun Content() {
        GameSearchContent()
    }
}

@Composable
fun GameSearchContent() {
    BoardGameScaffold(
        titlePage = R.string.search_bgg,
        selectedScreen = BottomBarElements.HomeButton.title

    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(paddingValues)
        ){

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource( R.drawable.bgg),
                contentDescription = "BGG LOGO",
                modifier = Modifier.fillMaxWidth())
        }

    }
}

@Preview
@Composable
fun GameSearchContentPreview(){
    GameSearchContent()
}