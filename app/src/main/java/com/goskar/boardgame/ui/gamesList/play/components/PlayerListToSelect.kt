package com.goskar.boardgame.ui.gamesList.play.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.gamesList.play.GamePlayState
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold18

@Composable
fun PlayerListToSelect(
    state: GamePlayState,
    selectedPlayer: (Player) -> Unit = {}
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        if (!state.playerList.isNullOrEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
            ) {
                items(items = state.playerList) { player ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var isChecked by remember { mutableStateOf(player.selected) }
                        Checkbox(
                            checked = isChecked, onCheckedChange = {
                            if(state.countSelectedPlayer != state.game?.maxPlayer?.toInt() || isChecked){
                                //DODAC zabezpiecznie gdy maxPlayer jest null lub ""
                                isChecked = it
                                selectedPlayer(player)
                            } else {
                                Toast.makeText(context, "Wybrano max graczy", Toast.LENGTH_LONG).show()
                            }
                        })
                        Text(
                            maxLines = 2,
                            style = Smooch16,
                            text = player.name,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } else {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "AddGamePlay",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        stringResource(R.string.history_empty_player_list_text),
                        style = SmoochBold18)
                }
            }

        }
    }
}

@Preview
@Composable
fun PlayerListToSelectPreview() {
    val player =
        Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
    val player2 =
        Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)
    val player3 =
        Player(name = "Maksymilian WIelki Trzy Linijkowy", winRatio = 2, games = 6, description = "ds", selected = true)
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            PlayerListToSelect(
                state = GamePlayState(playerList = listOf(player,player2, player2, player3))
            )
        }
    }
}

@Preview
@Composable
fun PlayerListToSelectPreviewWithoutPlayer() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            PlayerListToSelect(
                state = GamePlayState(playerList = emptyList())
            )
        }
    }
}