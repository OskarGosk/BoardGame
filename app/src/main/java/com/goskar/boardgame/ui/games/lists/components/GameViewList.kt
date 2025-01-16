package com.goskar.boardgame.ui.games.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.rest.models.Game

@Composable
fun GameViewList(
    gameList: List<Game>,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit = {}
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items = gameList) { game ->
            SingleGameCard(
                game = game,
                modifier = Modifier.padding(bottom = if(gameList.indexOf(game)==(gameList.size -1) ) 60.dp else 10.dp),
                deleteGame = deleteGame,
                refresh = refresh
            )
        }
    }
}


@Preview
@Composable
fun GameViewListPreview() {
    val game = Game(
        name = "Marvel",
        expansion = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    val game2 = Game(
        name = "Scrable",
        expansion = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    val gameList = listOf(game, game2, game)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        GameViewList (gameList = gameList)
    }
}