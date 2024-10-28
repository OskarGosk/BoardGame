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
import OGosk.boardgamebase.R
import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.OperationStatus
import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.viewModel.GameViewModel
import OGosk.boardgamebase.viewModel.PlayerViewModel
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddEditGames : ComponentActivity() {

    val gameViewModel by viewModel<GameViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editGames : Game? = intent.getSerializableExtra("edit_game") as? Game
        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameAddEditView(editGames)
                    observeAddTaskStatus()
                }
            }
        }
    }

    private fun observeAddTaskStatus() {
        when (gameViewModel.addEditGameStatus){
            OperationStatus.SUCCESS -> {
                val intent = Intent(this, GamesActivity::class.java)
                startActivity(intent)
                finish()
            }
            OperationStatus.ERROR -> {
                Toast.makeText(this,"Connection problem", Toast.LENGTH_LONG).show()
            }
            OperationStatus.LOADING, OperationStatus.UNKNOWN -> {}
        }
    }

    @Composable
    fun GameAddEditView(editGames: Game?) {
        val context = LocalContext.current

        var gameName by remember { mutableStateOf(editGames?.name ?:"")}
        var gameMinPlayer by remember { mutableStateOf(editGames?.minPlayer?:"") }
        var gameMaxPlayer by remember { mutableStateOf(editGames?.maxPlayer?:"") }
        var gameBase by remember { mutableStateOf(editGames?.baseGame?:"") }
        var isChecked by remember { mutableStateOf(false)}
        
        Column (
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                stringResource(id = R.string.new_game),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = gameName,
                onValueChange = {gameName = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.game_name))
                },
                singleLine = true)

            OutlinedTextField(
                value = gameMinPlayer,
                onValueChange = {gameMinPlayer = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.min_player))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = gameMaxPlayer,
                onValueChange = {gameMaxPlayer = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.max_player))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = !isChecked },
                )
                Text(stringResource(id = R.string.expansion))
            }
            if(isChecked)
            {
                OutlinedTextField(
                    value = gameBase,
                    onValueChange = {gameBase = it},
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(stringResource(id = R.string.base_game))
                    },
                    singleLine = true)
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (editGames == null) {
                        val game = Game(
                            name = gameName,
                            expansion = isChecked,
                            baseGame = gameBase,
                            minPlayer = gameMinPlayer,
                            maxPlayer = gameMaxPlayer,
                            games = 0
                        )
                        gameViewModel.addGame(game)
                    } else {
                        val game = editGames.copy(
                            name = gameName,
                            expansion = isChecked,
                            baseGame = gameBase,
                            minPlayer =  gameMinPlayer,
                            maxPlayer = gameMaxPlayer,
                            games = editGames.games
                        )
                        gameViewModel.editGame(game)
                    }
                },
                modifier = Modifier.fillMaxWidth()) {
                if(gameViewModel.addEditGameStatus == OperationStatus.LOADING) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                }else {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if (editGames == null) {
                            Text(
                                stringResource(id = R.string.add_board),
                                fontSize = 20.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add_board),
                                modifier = Modifier.size(25.dp)
                            )
                        } else {
                            Text(
                                stringResource(id = R.string.edit_game),
                                fontSize = 20.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.edit_game),
                                modifier = Modifier.size(25.dp)
                            )

                        }
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
//            PlaverAddEditView(null)
        }
    }
}


