package com.goskar.boardgame.ui.player

import OGosk.boardgamebase.view.AddEditPlayer
import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import org.koin.androidx.compose.koinViewModel

class PlayerListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: PlayerListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        PlayerListContent(
            state = state
        )
    }
}

@Composable
fun PlayerListContent(
    state: PlayerListState
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.player_list),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            if (state.playerList.isEmpty()){
                Text(
                    text = "Empty player list",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                PlayerViewList(state = state)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = {
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

@Composable
fun PlayerViewList(
    state: PlayerListState
) {
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        items(items = state.playerList) { player ->
            Card (
                modifier = Modifier.clickable {
                    isDialogVisible = true
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
            }
//            if (isDialogVisible) {
//                AlertDialog(
//                    onDismissRequest = { isDialogVisible = false },
//                    title = {
//                        Text("Select")
//                    },
//                    text = {
//                        Text("What want you do with this player? :")
//                    },
//                    confirmButton = {
//                        // Przycisk Edytuj
//                        IconButton(onClick = {
//                            val intent = Intent(context, AddEditPlayer::class.java)
//                            intent.putExtra("edit_player",player)
//                            startActivity(intent)
//                            isDialogVisible = false
//                        }) {
//                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
//                        }
//                    },
//                    dismissButton = {
//                        // Przycisk Usuń
//                        IconButton(onClick = {
//                            playerViewModel.deletePlayer(player)
//                            isDialogVisible = false
//
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete"
//                            )
//                        }
//                    }
//                )
//            }
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
        PlayerListContent(
            state = PlayerListState()
        )
    }
}