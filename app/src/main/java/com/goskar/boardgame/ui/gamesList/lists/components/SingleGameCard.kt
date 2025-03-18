package com.goskar.boardgame.ui.gamesList.lists.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleGameCard(
    game: Game,
    modifier: Modifier,
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
    onCardCLick: () -> Unit = {},
) {
    val context = LocalContext.current
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = modifier
            .height(200.dp)
            .padding(5.dp)
            .clickable {
                if (game.uri.isNullOrEmpty()) {
                    Toast.makeText(context, R.string.board_without_cover, Toast.LENGTH_LONG).show()
                } else {
                    onCardCLick()
                }
            }
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
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            )

            Row {
                Text(
                    text = stringResource(if (game.expansion) R.string.board_expansion else R.string.board_base),
                    style = Smooch14,
                )
                if(game.cooperate) {
                    Text(
                        text = stringResource(R.string.board_cooperate),
                        style = Smooch14,
                        modifier = Modifier.padding(start = 15.dp)

                    )
                }
            }

            GameDataRow(R.string.board_min_player, game.minPlayer)
            GameDataRow(R.string.board_max_player, game.maxPlayer)
            GameDataRow(R.string.board_how_many_played, "${game.games}")
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
        cooperate = true,
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
            SingleGameCard(game = game, Modifier)
        }
    }
}