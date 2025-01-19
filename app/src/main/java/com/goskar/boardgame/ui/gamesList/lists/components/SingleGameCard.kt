package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleGameCard(
    game: Game,
    modifier: Modifier,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = modifier
            .height(200.dp)
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
                Text(
                    text = game.name,
                    style = SmoochBold26,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            Text(
                stringResource(if (game.expansion) R.string.expansion else R.string.base_game),
                style = Smooch14
            )

            GameDataRow(R.string.min_player, game.minPlayer)
            GameDataRow(R.string.max_player, game.maxPlayer)
            GameDataRow(R.string.how_many_played, "${game.games}")
            ButtonRow(game = game, deleteGame, refresh)
        }
    }
}

@Preview
@Composable
fun SingleGameCardPreview() {

    val game = Game(
        name = "Marvel Marvel Marvel Marel Marvel Marvel Marvel Marvel Mar",
        expansion = false,
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
            SingleGameCard(game = game, Modifier)
        }
    }
}

@Preview
@Composable
fun SingleGameCardPreviewOneLine() {

    val game = Game(
        name = "Marvel Marvel ",
        expansion = false,
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
            SingleGameCard(game = game, Modifier)
        }
    }
}