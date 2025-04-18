package com.goskar.boardgame.ui.playerList.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.components.other.SimpleAlertDialog
import com.goskar.boardgame.ui.playerList.PlayerListState
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SinglePlayerCard(
    player: Player,
    modifier: Modifier,
    deletePlayer: (Player) -> Unit = {},
    update: (PlayerListState) -> Unit = {},
    addPlayer: (Boolean) -> Unit = {},
    state: PlayerListState,
    ) {
    var isExpanded by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showAddEditDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(15),
        modifier = modifier
            .clickable {
                isExpanded = !isExpanded
            }
            .padding(bottom = 10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = player.name,
                    style = SmoochBold26,
                    maxLines = if (isExpanded) 4 else 1,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(2.5f),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.player_games_count, player.games),
                    style = Smooch18,
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            WinLinearIndicator(
                progress = (player.winRatio.toFloat() / player.games),
                modifier = Modifier,
                isExpanded = isExpanded
            )
        }
        if (isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                IconButton(onClick = {
                    update (state.copy(
                        player = player
                    ))
                    showAddEditDialog = true
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                }
                IconButton(onClick = {
                    showAlertDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Game"
                    )
                }
            }
        }

        if (showAlertDialog) {
            SimpleAlertDialog(
                titleText = stringResource(R.string.delete, player.name),
                contentText = R.string.player_delete_info,
                modifierButton = Modifier.align(Alignment.CenterHorizontally),
                onDismiss = {showAlertDialog = false},
                confirmButtonClick = {
                    showAlertDialog = false
                    isExpanded = false
                    deletePlayer(player)
                }
            )
        }

        if(showAddEditDialog) {
            AddEditDialog(
                newPlayer = false,
                state = state,
                confirmButtonClick = {
                    showAddEditDialog = false
                    addPlayer(false)
                },
                onDismiss = {showAddEditDialog = !showAddEditDialog},
                update = update
            )
        }
    }
}

@Preview
@Composable
fun SinglePlayerCardPreview() {
    val player =
        Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SinglePlayerCard(player, modifier = Modifier, state = PlayerListState())
        }
    }
}

@Preview
@Composable
fun SinglePlayerCardPreview2() {
    val player =
        Player(
            name = "Maksymilian Bardzo długie imie i jeszcze ",
            winRatio = 2,
            games = 6,
            description = "ds",
            selected = true
        )

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SinglePlayerCard(player, modifier = Modifier, state = PlayerListState())
        }
    }
}