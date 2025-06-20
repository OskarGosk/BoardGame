package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gamesList.lists.GameListState

@Composable
fun GameViewList(
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
    state: GameListState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val newGameList: List<Game> = when (state.sortOption) {
            R.string.default_sort -> state.gameList ?: emptyList()
            R.string.name_ascending -> state.gameList?.sortedBy { it.name } ?: emptyList()
            R.string.name_descending -> state.gameList?.sortedByDescending { it.name }
                ?: emptyList()

            R.string.played_ascending -> state.gameList?.sortedBy { it.games } ?: emptyList()
            R.string.played_descending -> state.gameList?.sortedByDescending { it.games }
                ?: emptyList()

            else -> state.gameList ?: emptyList()
        }.filter { it.name.lowercase().contains(state.searchTxt.lowercase()) }
        items(items = newGameList, key = { it.id }) { game ->
            var isExpanded by rememberSaveable(game.id) { mutableStateOf(true) }
            val gameUri = game.uriFromBgg?:game.uri
            if (isExpanded && !gameUri.isNullOrEmpty()) {
                SingleCoverGameCard(
                    game = game,
                    modifier = Modifier.padding(
                        bottom = if (newGameList.indexOf(game) == (newGameList.size - 1)
                        ) 60.dp else 10.dp
                    ),
                    deleteGame = deleteGame,
                    refresh = refresh,
                    onCardCLick = { isExpanded = !isExpanded }
                )
            } else {
                SingleGameCard(
                    game = game,
                    modifier = Modifier.padding(
                        bottom = if (newGameList.indexOf(game) == (newGameList.size - 1)
                        ) 60.dp else 10.dp
                    ),
                    deleteGame = deleteGame,
                    refresh = refresh,
                    onCardCLick = { isExpanded = !isExpanded }
                )
            }

        }
    }
}


@Preview
@Composable
fun GameViewListPreview() {
    val game = Game(
        name = "Marvel",
        expansion = false,
        cooperate = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    val game2 = Game(
        name = "Scrable",
        expansion = true,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    val gameList = listOf(game, game2, game)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        GameViewList(state = GameListState(gameList))
    }
}