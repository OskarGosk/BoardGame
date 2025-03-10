package com.goskar.boardgame.ui.gamesList.play.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.gamesList.play.GamePlayState
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold16
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun GameInfo(
    state: GamePlayState
) {
    val navigator = LocalNavigator.current
    Row() {
        if(!state.game?.uri.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(state.game?.uri))
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(1f)
                    .size(150.dp)
                    .padding(10.dp)
            )
        }
        else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                .weight(1f)
                .size(150.dp)
                .padding(10.dp)) {
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = {
                        navigator?.push(AddEditGameScreen(state.game))
                    },
                    modifier = Modifier.fillMaxWidth()
                        .defaultMinSize(50.dp),
                ) {
                    Text(text = stringResource(R.string.history_game_without_cover),
                        style = SmoochBold18,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(
            Modifier
                .weight(1f)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                style = SmoochBold26,
                text = state.game?.name ?: "",
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                stringResource(if (state.game?.expansion != false) R.string.board_expansion else R.string.board_base),
                style = Smooch14
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    stringResource(R.string.board_min_player),
                    style = Smooch16
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = state.game?.minPlayer ?: "",
                    style = SmoochBold16
                )
                Spacer(modifier = Modifier.width(30.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    stringResource(R.string.board_max_player),
                    style = Smooch16
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = state.game?.maxPlayer ?: "",
                    style = SmoochBold16
                )
                Spacer(modifier = Modifier.width(30.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    stringResource(R.string.board_how_many_played),
                    style = Smooch16
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = state.game?.games.toString(),
                    style = SmoochBold16
                )
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
        cooperate = true,
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