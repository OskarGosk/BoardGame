package OGosk.boardgamebase.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import OGosk.boardgamebase.view.ui.theme.BoardGameTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.viewModel.PlayerViewModel
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddEditPlayer : ComponentActivity() {

    val playerViewModel by viewModel<PlayerViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editPlayer : Player? = intent.getSerializableExtra("edit_player") as? Player
        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlaverAddEditView(editPlayer)
                }
            }
        }
    }

    @Composable
    fun PlaverAddEditView(editPlayer: Player?) {
        val context = LocalContext.current

        var playerName by remember { mutableStateOf(editPlayer?.name ?:"")}
        var playerDescription by remember { mutableStateOf(editPlayer?.description ?:"") }
        
        Column (
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                stringResource(id = R.string.new_player),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = playerName,
                onValueChange = {playerName = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_name))
                },
                singleLine = true)

            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                value = playerDescription,
                onValueChange = {playerDescription = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_description))
                })
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (editPlayer == null) {
                        val player = Player(
                            name = playerName,
                            games = 0,
                            winRatio = 0,
                            description = playerDescription,
                            selected = false
                        )
                        playerViewModel.addPlayer(player)
                    } else {
                        val player = editPlayer.copy(
                            name = playerName,
                            games = editPlayer.games,
                            winRatio = editPlayer.winRatio,
                            description = playerDescription,
                            selected = false
                        )
                        playerViewModel.editPlayer(player)
                    }

                    val intent = Intent(context, PlayersActivity::class.java)
                    context.startActivity(intent)
                    finish()
                },
                modifier = Modifier.fillMaxWidth()) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (editPlayer == null){
                        Text(stringResource(id = R.string.add_player),
                            fontSize = 20.sp)
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_player),
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.edit_player),
                            fontSize = 20.sp)
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_player),
                            modifier = Modifier.size(25.dp)
                        )
                    }

                }
            }
        }
    }

    @Preview
    @Composable
    fun addPlayerPreview(){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaverAddEditView(null)
        }
    }
}


