package com.goskar.boardgame.ui.player.playerList.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.player.playerList.PlayerListState

@Composable
fun PlayerViewList(
    deletePlayer: (Player) -> Unit = {},
    refreshPlayer: () -> Unit = {},
    update: (PlayerListState) -> Unit = {},
    addPlayer: (Boolean) -> Unit = {},
    state: PlayerListState
) {
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(vertical = 10.dp),
    )
    {
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

        items(items = newPlayerList) { player ->
            if (player.name.lowercase().contains(state.searchTxt.lowercase())) {
                SinglePlayerCard(
                    player = player,
                    modifier = Modifier.padding(bottom = if (newPlayerList.indexOf(player) == (newPlayerList.size - 1)) 50.dp else 0.dp),
                    deletePlayer = deletePlayer,
                    refreshPlayer = refreshPlayer,
                    addPlayer = addPlayer,
                    state = state,
                    update = update
                )
            }
        }

    }
}

@Preview
@Composable
fun PlayerListViewPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val player =
            Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
        val player2 =
            Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)
        PlayerViewList(
            state = PlayerListState(
                playerList = listOf(
                    player,
                    player2,
                    player2,
                    player,
                    player,
                    player2,
                    player2,
                    player,
                    player2
                )
            )
        )
    }
}