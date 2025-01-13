package com.goskar.boardgame.ui.games.play

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import com.goskar.boardgame.data.rest.models.Player
import com.goskar.boardgame.ui.player.PlayerListViewModel
import com.goskar.boardgame.ui.theme.BoardGameTheme
import java.time.LocalDate
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import org.koin.androidx.compose.koinViewModel
import pl.ecp.app.ui.components.scaffold.BoardGameScaffold

class GamePlayActivityScreen(
    val game: Game
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: GamePlayViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val playerViewModel: PlayerListViewModel = koinViewModel()
        val playerState by playerViewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.update(
                state.copy(
                    game = game
                )
            )
            playerViewModel.getAllPlayer()
        }

        BoardGameTheme {
            GamePlayContent(
                state = state,
                playerList = playerState.playerList,
                selectedPlayer = playerViewModel::selectedPlayer
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayContent(
    state: GamePLayState,
    playerList: List<Player>,
    selectedPlayer: (Player) -> Unit = {},
) {

//    val gameViewModel : GameListViewModel = koinViewModel()
//    val playerViewModel : PlayerListViewModel = koinViewModel()

//    try {
//        playerViewModel.getAllPlayer()
//    } catch (e: Exception) {
//        Log.d ("Game","Bład wczytania listy graczy --- $e")
//    }


//    val playGame : Game = intentent.getSerializableExtra("play_game") as Game

    val calendarState = rememberSheetState()
    var playGameDate by remember { mutableStateOf(LocalDate.now()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            playGameDate = date
        })
    /*
    Na górze wybrana gra z możliwością edycji?
    Dodawanie playerów którzy grali
    Kto wygrał
    Data gry

    Korzytam z viewMOdel player,game
    hmm czy daty zapisywać w nowej tebeli?

    póki co jest do dopasowania widok
     */

    BoardGameScaffold(
        titlePage = stringResource(R.string.board_list)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(10.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState),

            ) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Marvel",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = state.game?.name ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                        )
                        if (state.game?.expansion
                                ?: true
                        ) Text(text = "expansion") else Text(text = "base")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Min player:")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = state.game?.minPlayer ?: "")
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Max player:")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = state.game?.maxPlayer ?: "")
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "How many played:")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = state.game?.games.toString())
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(items = playerList) { player ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var isChecked by remember { mutableStateOf(player.selected) }
                            Checkbox(checked = isChecked, onCheckedChange = {
                                isChecked = it
                                selectedPlayer(player)
                            })
                            Text(text = player.name)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        calendarState.show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
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
            val selectedPlayers = playerList.filter { it.selected == true }
            var expanded by remember { mutableStateOf(false) }
            var winner by remember { mutableStateOf("winner") }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
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
                        readOnly = true,
                        value = winner,
                        onValueChange = {},
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = MaterialTheme.colorScheme.background,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        selectedPlayers.forEach { player ->
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

            Text(
                text = "Game Description:",
                modifier = Modifier
                    .fillMaxWidth()
            )
            var descriptionGame by remember { mutableStateOf("") }
            OutlinedTextField(
                value = descriptionGame,
                onValueChange = { descriptionGame = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(200.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
//                playerViewModel.playerList.filter { it.selected }.forEach { player ->
//                    val playerGames = player.copy(
//                        name = player.name,
//                        games = player.games + 1,
//                        winRatio = if (player.name == winner) player.winRatio + 1 else player.winRatio,
//                        description = player.description,
//                        selected = false
//                    )
//                    playerViewModel.editPlayer(playerGames)
//                }
//                val game = playGame.copy(
//                    name = playGame.name,
//                    expansion = playGame.expansion,
//                    baseGame = playGame.baseGame,
//                    minPlayer = playGame.minPlayer,
//                    maxPlayer = playGame.maxPlayer,
//                    games = playGame.games + 1
//                )
//                gameViewModel.editGame(game)

                    navigator?.pop()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "AddGamePlay",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(text = "Add New GamePlay")
                }
            }
            Row {
                Text(text = "Row z przyciskami")
            }

        }
    }
}

@Preview
@Composable
fun GamePlayActivityPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val player =
            Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)
        val player2 =
            Player(name = "Kamila", winRatio = 2, games = 6, description = "ds", selected = false)

        val game = Game(
            name = "Nazwa Testowa",
            expansion = true,
            baseGame = "Gra bazowa",
            minPlayer = "1",
            maxPlayer = "4",
            games = 6,
            id = "5456"
        )
        GamePlayContent(GamePLayState(game), listOf(player, player, player2))
    }
}