package com.goskar.boardgame.ui.games.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.games.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.games.play.GamePlayActivityScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonRow(
    game: Game,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit = {}
) {
    val navigator = LocalNavigator.current


    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {

            IconButton(onClick = {
                navigator?.push(GamePlayActivityScreen(game))
            }) {

                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add to play",
                    Modifier.size(35.dp)
                )
            }

            IconButton(onClick = {
                navigator?.push(AddEditGameScreen(game))
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Game",
                    Modifier.size(35.dp)
                )
            }
            IconButton(onClick = {
                deleteGame(game.id)
                refresh()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Game",
                    Modifier.size(35.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ButtonRowPreview() {
    val game = Game(
        name = "Marvel",
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
        ButtonRow(game)
    }
}