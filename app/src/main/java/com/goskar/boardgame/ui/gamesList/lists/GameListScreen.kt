package com.goskar.boardgame.ui.gamesList.lists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.components.other.EmptyListWithButton
import com.goskar.boardgame.ui.components.other.FloatingMenu
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.gamesList.lists.components.GameViewList
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.gamesList.lists.components.AddItemsMenu
import com.goskar.boardgame.ui.gamesList.lists.components.GameSearchRow

class GameListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: GameListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        BoardGameTheme {
            GameListContent(
                state = state,
                deleteGame = viewModel::validateDeleteGame,
                refresh = viewModel::refresh,
                update = viewModel::update
            )
        }
    }
}

@Composable
fun GameListContent(
    state: GameListState,
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
    update: (GameListState) -> Unit = {},
) {
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current


    BoardGameScaffold(
        titlePage = stringResource(R.string.board_list),
        selectedScreen = BottomBarElements.GameListButton.title,
        floatingActionButton = {
            FloatingMenu(items = AddItemsMenu.entries.map { it.items })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.gameList.isNullOrEmpty()) {
                EmptyListWithButton(
                    headerText = R.string.board_empty_list,
                    infoText = R.string.board_empty_list_add,
                    buttonText = R.string.board_add,
                    onClick = {
                        navigator?.push(AddEditGameScreen(null))
                    },
                    secondButtonText = R.string.board_bgg_add,
                    onClickSecondButton = {
                        navigator?.push(GameSearchScreen())
                    }
                )
            } else {
                GameSearchRow(update = update, state = state)
                GameViewList(deleteGame = deleteGame, refresh = refresh, state = state)
            }


        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp),
            contentAlignment = Alignment.BottomStart
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(R.drawable.bgg),
                    contentScale = ContentScale.Fit,
                    contentDescription = "BGG LOGO",
                    modifier = Modifier
                        .height(50.dp)
                        .clickable {
                            uriHandler.openUri("https://boardgamegeek.com/")
                        })

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
        cooperate = true,
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