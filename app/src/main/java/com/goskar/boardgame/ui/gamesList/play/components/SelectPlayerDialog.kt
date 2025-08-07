package com.goskar.boardgame.ui.gamesList.play.components

import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.components.other.SearchRowGlobal
import com.goskar.boardgame.ui.gamesList.play.GamePlayState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.Smooch16

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectPlayerDialog(
    state: GamePlayState,
    selectedPlayer: (Player) -> Unit = {},
    update: (GamePlayState) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    val newPlayerList: List<Player> = when (state.sortOption) {
        R.string.default_sort -> state.playerList ?: emptyList()
        R.string.name_ascending -> state.playerList?.sortedBy { it.name } ?: emptyList()
        R.string.name_descending -> state.playerList?.sortedByDescending { it.name }
            ?: emptyList()

        R.string.played_ascending -> state.playerList?.sortedBy { it.games } ?: emptyList()
        R.string.played_descending -> state.playerList?.sortedByDescending { it.games }
            ?: emptyList()

        else -> state.playerList ?: emptyList()
    }.filter { it.name.lowercase().contains(state.searchTxt.lowercase()) }


    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Column(
            modifier = Modifier
                .onPreInterceptKeyBeforeSoftKeyboard { event ->
                    if (event.key.nativeKeyCode == KeyEvent.KEYCODE_BACK) {
                        focusManager.clearFocus()
                        true
                    } else {
                        false
                    }
                }
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
                .padding(10.dp),
        ) {
            SearchRowGlobal(
                searchHelp = R.string.player_name,
                searchTxt = state.searchTxt,
                sortOption = state.sortOption,
                updateTxt = {
                    update(
                        state.copy(
                            searchTxt = it
                        )
                    )
                },
                clearTxt = {
                    update(
                        state.copy(
                            searchTxt = ""
                        )
                    )
                },
                updateSort = {
                    update(
                        state.copy(
                            sortOption = it
                        )
                    )
                }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(items = newPlayerList) { player ->
                    val onClick = {
                        if (state.countSelectedPlayer != state.game?.maxPlayer?.toInt() || player.selected) {
                            //DODAC zabezpiecznie gdy maxPlayer jest null lub ""
                            selectedPlayer(player)
                        } else {
                            Toast.makeText(
                                context,
                                "Wybrano max graczy",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick()
                            }
                    ) {
                        Checkbox(
                            checked = player.selected, onCheckedChange = {
                                onClick()
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
        }


    }

}

@Preview()
@Composable
fun SelectPlayerDialogPreview() {
    val player =
        Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
    val player2 =
        Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)
    val player3 =
        Player(
            name = "Maksymilian WIelki Trzy Linijkowy",
            winRatio = 2,
            games = 6,
            description = "ds",
            selected = true
        )
    BoardGameTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(10.dp)) {

                SelectPlayerDialog(
                    state = GamePlayState(playerList = listOf(player, player2, player3))
                )
            }
        }
    }
}