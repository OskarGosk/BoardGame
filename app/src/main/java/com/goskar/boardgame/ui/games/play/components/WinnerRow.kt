package com.goskar.boardgame.ui.games.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Player
import com.goskar.boardgame.ui.games.play.GamePlayState
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinnerRow(
    state: GamePlayState,
    update: (GamePlayState) -> Unit = {}
) {
    val selectedPlayers = state.playerList?.filter { it.selected }
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.winner),
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            style = Smooch18,
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                textStyle = SmoochBold18,
                modifier = Modifier
                    .menuAnchor(),
                readOnly = true,
                value = state.winner,
                onValueChange = {},
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                selectedPlayers?.forEach { player ->
                    DropdownMenuItem(
                        text = { Text(text = player.name) },
                        onClick = {
                            update(
                                state.copy(
                                    winner = player.name
                                )
                            )
                            expanded = false
                        })
                }
            }
        }
    }
}

@Preview
@Composable
fun WinnerRowPreview() {
    val player =
        Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
    val player2 =
        Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            WinnerRow(
                state = GamePlayState(playerList = listOf(player,player2, player2))
            )
        }
    }
}