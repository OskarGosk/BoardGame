package com.goskar.boardgame.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Player
import com.goskar.boardgame.ui.player.addEditPlayer.AddEditPlayerScreen
import org.koin.androidx.compose.koinViewModel
import pl.ecp.app.ui.components.scaffold.BoardGameScaffold

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
            update = viewModel::update,
            deletePlayer = viewModel::validateDeletePlayer,
            refreshPlayer = viewModel::getAllPlayer
        )
    }
}

@Composable
fun PlayerListContent(
    state: PlayerListState,
    update: (PlayerListState) -> Unit = {},
    deletePlayer: (String) -> Unit = {},
    refreshPlayer: () -> Unit = {}
) {
    val navigator = LocalNavigator.current

    BoardGameScaffold(
        titlePage = stringResource(id = R.string.player_list)
        ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize()
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
                    if (state.playerList.isNullOrEmpty()) {
                        Text(
                            text = "Empty player list",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        PlayerViewList(
                            playerList = state.playerList,
                            deletePlayer = deletePlayer,
                            refreshPlayer = refreshPlayer
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = {
                            navigator?.push(AddEditPlayerScreen(null))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_player)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerViewList(
    playerList: List<Player>,
    deletePlayer: (String) -> Unit = {},
    refreshPlayer: () -> Unit = {}
) {
    val navigator = LocalNavigator.current
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        items(items = playerList) { player ->
            var isExpanded by remember { mutableStateOf(false) }
            Card (
                modifier = Modifier.clickable {
                    isExpanded = !isExpanded
                }
            ){
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                    ) {
                        Text(text = player.name)
                        Text(text = player.description)
                    }
                    Column (
                        modifier = Modifier
                            .padding(10.dp)
                    ){
                        Text(text = player.games.toString())
                        val result = player.winRatio.toFloat() / player.games
                        if (player.winRatio == 0) Text(text = "0") else Text(text = "%.2f".format(result))
                    }

                }
                if(isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        IconButton(onClick = {
                            navigator?.push(AddEditPlayerScreen(player))
                            isExpanded = false
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                        }
                        IconButton(onClick = {
                            deletePlayer(player.id)
                            refreshPlayer()
                            isExpanded = false
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
            state = PlayerListState(playerList = listOf(player, player2))
        )
    }
}