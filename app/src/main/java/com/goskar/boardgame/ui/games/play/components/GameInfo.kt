package com.goskar.boardgame.ui.games.play.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.games.play.GamePlayState

@Composable
fun GameInfo(
    state: GamePlayState
) {
    Row() {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Marvel",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        )
        Column(
            Modifier.weight(1f)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = state.game?.name ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Text(stringResource(if(state.game?.expansion != false) R.string.expansion else R.string.base_game))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(stringResource(R.string.min_player))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = state.game?.minPlayer ?: "")
                Spacer(modifier = Modifier.width(30.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(stringResource(R.string.max_player))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = state.game?.maxPlayer ?: "")
                Spacer(modifier = Modifier.width(30.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(stringResource(R.string.how_many_played))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = state.game?.games.toString())
                Spacer(modifier = Modifier.width(30.dp))
            }
        }
    }
}

@Preview
@Composable
fun SearchRowPreview() {
    val game = Game(
        name = "Nazwa Testowa",
        expansion = true,
        baseGame = "Gra bazowa",
        minPlayer = "1",
        maxPlayer = "4",
        games = 6,
        id = "5456"
    )
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            GameInfo(
                state = GamePlayState(game = game)
            )
        }
    }
}