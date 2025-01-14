package com.goskar.boardgame.ui.games.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.games.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.games.lists.components.GameViewList
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel
import pl.ecp.app.ui.components.scaffold.BoardGameScaffold

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
                refresh = viewModel::getAllGame
            )
        }
    }
}

@Composable
fun GameListContent(
    state: GameListState,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit = {}
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
                Text(
                    stringResource(R.string.empty_game_list),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                )
                Text(
                    stringResource(R.string.add_more_game),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                Button(
                    onClick = {navigator?.push(AddEditGameScreen(null))},
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        stringResource(R.string.add_board)
                    )
                }
            } else {
                GameViewList(state.gameList, deleteGame, refresh)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(onClick = {
                navigator?.push(AddEditGameScreen(null))
            }) {
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