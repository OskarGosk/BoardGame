package com.goskar.boardgame.ui.games.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.ui.games.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.games.play.GamePlayActivityScreen
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
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.gameList.isEmpty()) {
                Text(
                    text = "Empty game list",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                GameViewList(state, deleteGame, refresh)
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

@Composable
fun GameViewList(
    state: GameListState,
    deleteGame: (String) -> Unit = {},
    refresh: () -> Unit
) {
    val navigator = LocalNavigator.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 60.dp)
    ) {
        items(items = state.gameList) { game ->
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .height(200.dp)
                    .padding(5.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = game.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                    if (game.expansion) Text(text = "expansion") else Text(text = "base")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "Min player")
                        Text(text = game.minPlayer)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "Max player")
                        Text(text = game.maxPlayer)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "How many played")
                        Text(text = "${game.games}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    )
                    {
                        IconButton(onClick = {
                            navigator?.push(GamePlayActivityScreen())
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to play"
                            )
                        }
                        IconButton(onClick = {
                            navigator?.push(AddEditGameScreen(game))

                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                        }
                        IconButton(onClick = {
                            deleteGame(game.id)
                            refresh()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Game"
                            )
                        }
                    }

                }
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