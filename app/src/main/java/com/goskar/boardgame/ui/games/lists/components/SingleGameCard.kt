package com.goskar.boardgame.ui.games.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game

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
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis

            )
            Text (
                stringResource(if(game.expansion) R.string.expansion else R.string.base_game),
                fontSize = 12.sp)

            GameDataRow(R.string.min_player, game.minPlayer)
            GameDataRow(R.string.max_player, game.maxPlayer)
            GameDataRow(R.string.how_many_played, "${game.games}")
            ButtonRow(game = game, deleteGame, refresh)
        }
    }
}

@Preview
@Composable
fun SingleGameCardPreview(){

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