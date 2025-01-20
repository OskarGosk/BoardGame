package com.goskar.boardgame.ui.gamesList.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.gamesList.lists.components.GameViewList
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.gamesList.lists.components.EmptyGameList
import com.goskar.boardgame.ui.gamesList.lists.components.GameSearchRow

class GameListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: GameListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getAllGame()
        }

        BoardGameTheme {
            GameListContent(
                state = state,
                deleteGame = viewModel::validateDeleteGame,
                refresh = viewModel::getAllGame,
                update = viewModel::update
            )
        }
    }
}

@Composable
fun GameListContent(
    state: GameListState,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit = {},
    update: (GameListState) -> Unit = {}
) {
    val navigator = LocalNavigator.current

    BoardGameScaffold(
        titlePage = stringResource(R.string.board_list)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.gameList.isNullOrEmpty()) {
                EmptyGameList()
            } else {
                GameSearchRow(update = update, state = state)
                GameViewList(deleteGame = deleteGame,refresh = refresh, state = state)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
                    navigator?.push(AddEditGameScreen(null))
                },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_board)
                )
            }
        }
    }
}


@Preview
@Composable
fun GameListContentPreview() {
    val game = Game(
        name = "Marvel",
        expansion = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    val gameList = listOf(game, game, game)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        GameListContent(state = GameListState(gameList = gameList))
    }
}

@Preview
@Composable
fun GameListEmptyPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        GameListContent(state = GameListState(gameList = emptyList()))
    }
}