package OGosk.boardgamebase.view

import OGosk.boardgamebase.model.Game
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import OGosk.boardgamebase.view.ui.theme.BoardGameTheme
import OGosk.boardgamebase.viewModel.GameViewModel
import OGosk.boardgamebase.viewModel.PlayerViewModel
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
//import com.maxkeppeker.sheets.core.models.base.rememberSheetState
//import com.maxkeppeler.sheets.calendar.CalendarDialog
//import com.maxkeppeler.sheets.calendar.models.CalendarConfig
//import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate

class GamePlayActivity : ComponentActivity() {

    private val gameViewModel by viewModel<GameViewModel>()
    private val playerViewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            playerViewModel.getAllPlayer()
        } catch (e: Exception) {
            Log.d ("Game","Bład wczytania listy graczy --- $e")
        }

        val playGame : Game = intent.getSerializableExtra("play_game") as Game
        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameplayView(playGame)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun GameplayView (playGame: Game) {

//        val calendarState = rememberSheetState()
        var playGameDate by remember { mutableStateOf(LocalDate.now())}
//        val keyboardController = LocalSoftwareKeyboardController.current
        val scrollState = rememberScrollState()
        val context = LocalContext.current

//        CalendarDialog(
//            state = calendarState,
//            config = CalendarConfig(
//                monthSelection = true,
//                yearSelection = true,
//            ),
//            selection = CalendarSelection.Date { date ->
//                Log.d ("Oskar","$date")
//                playGameDate = date
//            })
        /*
        Na górze wybrana gra z możliwością edycji?
        Dodawanie playerów którzy grali
        Kto wygrał
        Data gry

        Korzytam z viewMOdel player,game
        hmm czy daty zapisywać w nowej tebeli?

        póki co jest do dopasowania widok
         */

        Column (
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(scrollState),

        ){
            Row (
//                modifier = Modifier.weight(0.5f)
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Marvel",
                    modifier = Modifier.weight(1f)
                )
                Column (
                    modifier = Modifier.weight(1f)
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = playGame.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                        )
                        if (playGame.expansion) Text(text = "expansion") else Text(text = "base")
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "Min player")
                            Text(text = playGame.minPlayer)
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "Max player")
                            Text(text = playGame.maxPlayer)
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "How many played")
                            Text(text = "${playGame.games}")
                        }
                    }
                }
            }
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp)){
                    items(items = playerViewModel.playerList) { player ->
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            var isChecked by remember { mutableStateOf(player.selected)}
                            Checkbox(checked = isChecked, onCheckedChange = {
                                isChecked = it
                                playerViewModel.selectedPlayer(player)
                            })
                            Text(text = player.name)
                        }
                    }
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
//                Text(text = "$playGameDate")
//                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
//                    calendarState.show()
                },
                    modifier = Modifier
                        .fillMaxWidth()) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Play data: ",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "$playGameDate",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Log.d("Oskar","${playGameDate.toString()}")


            val selectedPlayers = playerViewModel.playerList.filter { it.selected }
            var expanded by remember { mutableStateOf(false) }
            var winner by remember { mutableStateOf("winner")}
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Winner: ",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor(),
//                            .fillMaxWidth(), // menuAnchor modifier mus be passed to the text field for correctnesst.
                        readOnly = true,
                        value = winner,
                        onValueChange = {},
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = MaterialTheme.colorScheme.background,)
                    )

                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        selectedPlayers.forEach {player ->
                            DropdownMenuItem(
                                text = { Text(text = player.name) },
                                onClick = {
                                    winner = player.name
                                    expanded = false
                                })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Log.d("Oskar", "${playerViewModel.playerList.filter { it.selected }}")

            Text(text = "Game Description:",
                modifier = Modifier
                    .fillMaxWidth())
            var descriptionGame by remember { mutableStateOf("")}
            OutlinedTextField(
                value = descriptionGame,
                onValueChange = { descriptionGame = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(200.dp))
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    playerViewModel.playerList.filter { it.selected }.forEach { player ->
                              val playerGames = player.copy(
                                  name = player.name,
                                  games = player.games + 1,
                                  winRatio = if (player.name == winner) player.winRatio + 1 else player.winRatio,
                                  description = player.description,
                                  selected = false
                              )
                              playerViewModel.editPlayer(playerGames)
                          }
                    val game = playGame.copy(
                        name = playGame.name,
                        expansion = playGame.expansion,
                        baseGame = playGame.baseGame,
                        minPlayer =  playGame.minPlayer,
                        maxPlayer = playGame.maxPlayer,
                        games = playGame.games + 1
                    )
                    gameViewModel.editGame(game)

                    val intent= Intent(context, GamesActivity::class.java)
                    context.startActivity(intent)
                    finish()
                },
                modifier = Modifier.fillMaxWidth(),) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "AddGamePlay",
                        modifier = Modifier.size(25.dp))
                    Text(text = "Add New GamePlay")
                }
            }
            Row {
                Text(text = "Row z przyciskami")
            }

        }
    }

    @Composable
    fun LastGameplay () {

    }

    @Preview
    @Composable
    fun GameplayViewPreview () {
        BoardGameTheme {
            val sampleGame = Game(
                name = "Sample Game",
                expansion = false,
                baseGame = "Base Game",
                minPlayer = "2",
                maxPlayer = "4",
                games = 1
            )
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                GameplayView(sampleGame)
            }
        }
    }
}