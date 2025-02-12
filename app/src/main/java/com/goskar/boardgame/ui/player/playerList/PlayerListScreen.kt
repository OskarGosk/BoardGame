package com.goskar.boardgame.ui.player.playerList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.other.EmptyListWithButton
import com.goskar.boardgame.ui.player.playerList.components.SearchRow
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.player.playerList.components.AddEditDialog
import com.goskar.boardgame.ui.player.playerList.components.PlayerViewList

class PlayerListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: PlayerListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getAllPlayer()
        }
        PlayerListContent(
            state = state,
            deletePlayer = viewModel::validateDeletePlayer,
            update = viewModel::update,
            addPlayer = viewModel::validateAddEditPLayer
        )
    }
}

@Composable
fun PlayerListContent(
    state: PlayerListState,
    deletePlayer: (Player) -> Unit = {},
    update: (PlayerListState) -> Unit = {},
    addPlayer: (Boolean) -> Unit = {},
    ) {
    var showAddEditDialog by remember { mutableStateOf(false) }

    BoardGameScaffold(
        titlePage = R.string.player_list,
        selectedScreen = BottomBarElements.PlayerListButton.title
    ) { paddingValues ->
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
                    SearchRow(
                        update = update,
                        state = state
                    )
                    if (state.playerList.isNullOrEmpty()) {
                        EmptyListWithButton(
                            headerText = R.string.player_empty_list,
                            infoText = R.string.player_empty_list_add,
                            buttonText = R.string.player_add,
                            onClick = { showAddEditDialog = true },
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
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = {
                            showAddEditDialog = true
                        },
                        modifier = Modifier
                            .size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.player_add)
                        )
                    }
                }
            }
        }

        if(showAddEditDialog) {
            AddEditDialog(
                newPlayer = true,
                state = state,
                confirmButtonClick = {
                    showAddEditDialog = false
                    addPlayer(true)
                },
                onDismiss = {showAddEditDialog = !showAddEditDialog},
                update = update
            )
        }
    }
}


@Preview
@Composable
fun PlayerListContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
            )
        )
    }
}

@Preview
@Composable
fun PlayerListEmptyContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PlayerListContent(
            state = PlayerListState()
        )
    }
}