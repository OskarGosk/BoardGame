package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.gamesList.play.GamePlayActivityScreen
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold22

@Composable
fun ButtonRow(
    game: Game,
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {}
) {
    var showAlertDialog by remember { mutableStateOf(false) }
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
                    Modifier.size(25.dp)
                )
            }

            IconButton(onClick = {
                navigator?.push(AddEditGameScreen(game))
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Game",
                    Modifier.size(25.dp)
                )
            }
            IconButton(onClick = {
                showAlertDialog = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Game",
                    Modifier.size(25.dp)
                )
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = {
                Text(
                    stringResource(R.string.delete, game.name),
                    style = SmoochBold22
                )
            },
            text = {
                Text(
                    stringResource(R.string.board_delete_info),
                    style = Smooch16
                )
            },
            confirmButton = {
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = {
                        deleteGame(game)
                        refresh()
                        showAlertDialog = false
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                ) {
                    Text(
                        stringResource(R.string.confirm),
                        style = SmoochBold18
                    )
                }
            },
            dismissButton = {
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = { showAlertDialog = false },
                    modifier = Modifier
                        .padding(top = 10.dp)
                ) {
                    Text(
                        stringResource(R.string.back),
                        style = SmoochBold18
                    )
                }
            }
        )
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