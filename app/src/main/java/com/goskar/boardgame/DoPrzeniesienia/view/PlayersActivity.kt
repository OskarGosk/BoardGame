package OGosk.boardgamebase.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import OGosk.boardgamebase.view.ui.theme.BoardGameTheme
import OGosk.boardgamebase.viewModel.PlayerViewModel
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayersActivity : ComponentActivity() {

    private val playerViewModel by viewModel<PlayerViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            playerViewModel.getAllPlayer()
        } catch (e: Exception) {
            Log.d ("Game","Bład wczytania listy graczy --- $e")
        }

        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerView()
                }
            }
        }
    }


    @Composable
    fun PlayerView() {
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
                if (playerViewModel.playerList.isEmpty()){
                    Text(
                        text = "Empty task list",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    PlayerViewList()
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(onClick = {
                    val intent = Intent(context, AddEditPlayer::class.java)
                    context.startActivity(intent)
//                finish()
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_player)
                    )
                }
            }
        }
    }

    @Composable
    fun PlayerViewList() {
        val context = LocalContext.current
        var isDialogVisible by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        )
        {
            items(items = playerViewModel.playerList) { player ->
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
                if (isDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { isDialogVisible = false },
                        title = {
                            Text("Select")
                        },
                        text = {
                            Text("What want you do with this player? :")
                        },
                        confirmButton = {
                            // Przycisk Edytuj
                            IconButton(onClick = {
                                val intent = Intent(context, AddEditPlayer::class.java)
                                intent.putExtra("edit_player",player)
                                startActivity(intent)
                                isDialogVisible = false
                            }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                            }
                        },
                        dismissButton = {
                            // Przycisk Usuń
                            IconButton(onClick = {
                                playerViewModel.deletePlayer(player)
                                isDialogVisible = false

                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    )
                }
            }

        }
    }


    @Preview
    @Composable
    fun Player_preview() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlayerView()
        }
    }
}
