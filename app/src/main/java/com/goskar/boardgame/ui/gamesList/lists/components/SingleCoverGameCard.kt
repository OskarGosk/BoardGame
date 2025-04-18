package com.goskar.boardgame.ui.gamesList.lists.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.data.models.Game

@Composable
fun SingleCoverGameCard(
    game: Game,
    modifier: Modifier,
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
    onCardCLick: () -> Unit = {},
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = modifier
            .height(200.dp)
            .padding(5.dp)
            .clickable {
                onCardCLick()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val gameUri = game.uriFromBgg?:game.uri
            if(!gameUri.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(gameUri))
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .size(150.dp)
                        .padding(10.dp)
                )
            }
            ButtonRow(game = game, deleteGame, refresh)
        }
    }
}

@Preview
@Composable
fun SingleCoverGameCardPreview() {

    val game = Game(
        name = "Marvel Marvel Marvel Marel Marvel Marvel Marvel Marvel Mar",
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SingleCoverGameCard(game = game, Modifier)
        }
    }
}

@Preview
@Composable
fun SingleCoverGameCardPreviewOneLine() {

    val game = Game(
        name = "Marvel Marvel ",
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SingleCoverGameCard(game = game, Modifier)
        }
    }
}