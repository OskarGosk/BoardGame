package com.goskar.boardgame.ui.playerList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.other.EmptyListWithButton
import com.goskar.boardgame.ui.components.other.SearchRowGlobal
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.bottomBar.BottomBarElements
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.playerList.components.AddEditDialog
import com.goskar.boardgame.ui.playerList.components.PlayerViewList

class PlayerListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: PlayerListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getAllPlayer()
        }
        BoardGameScaffold(
            titlePage = stringResource(R.string.player_list),
            selectedScreen = BottomBarElements.PlayerListButton.title,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.update(
                            state.copy(
                                showAddEditDialog = true
                            )
                        )
                    },
                    modifier = Modifier
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.player_add)
                    )
                }
            },
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase
        ) { paddingValues ->
            PlayerListContent(
                state = state,
                deletePlayer = viewModel::validateDeletePlayer,
                update = viewModel::update,
                addPlayer = viewModel::validateAddEditPLayer,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun PlayerListContent(
    state: PlayerListState,
    deletePlayer: (Player) -> Unit = {},
    update: (PlayerListState) -> Unit = {},
    addPlayer: (Boolean) -> Unit = {},
    paddingValues: PaddingValues
) {
    if (state.isLoading) AppLoader()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
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
                if (state.playerList.isNullOrEmpty()) {
                    EmptyListWithButton(
                        headerText = R.string.player_empty_list,
                        infoText = R.string.player_empty_list_add,
                        buttonText = R.string.player_add,
                        onClick = {
                            update(
                                state.copy(
                                    showAddEditDialog = true
                                )
                            )
                        },
                    )
                } else {
                    PlayerViewList(
                        deletePlayer = deletePlayer,
                        addPlayer = addPlayer,
                        update = update,
                        state = state
                    )
                }
            }
        }
    }

    if (state.showAddEditDialog) {
        AddEditDialog(
            newPlayer = true,
            state = state,
            confirmButtonClick = {
                update(
                    state.copy(
                        showAddEditDialog = false
                    )
                )
                addPlayer(true)
            },
            onDismiss = {
                update(
                    state.copy(
                        showAddEditDialog = false
                    )
                )
            },
            update = update
        )
    }
}


@Preview
@Composable
fun PlayerListContentPreview() {
    BoardGameScaffold(
        titlePage = stringResource(R.string.player_list),
        selectedScreen = BottomBarElements.PlayerListButton.title,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.player_add)
                )
            }
        },
    ) { paddingValues ->
        val player =
            Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
        val player2 =
            Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)
        PlayerListContent(
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
            ),
            paddingValues = paddingValues
        )
    }
}

@Preview
@Composable
fun PlayerListEmptyContentPreview() {
    BoardGameScaffold(
        titlePage = stringResource(R.string.player_list),
        selectedScreen = BottomBarElements.PlayerListButton.title,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.player_add)
                )
            }
        },
    ) { paddingValues ->
        PlayerListContent(
            state = PlayerListState(), paddingValues = paddingValues
        )
    }
}