package com.goskar.boardgame.ui.gamesList.play

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.Player
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gamesList.play.components.CooperateRow
import com.goskar.boardgame.ui.gamesList.play.components.GameInfo
import com.goskar.boardgame.ui.gamesList.play.components.PlayerListToSelect
import com.goskar.boardgame.ui.gamesList.play.components.SelectPlayerDialog
import com.goskar.boardgame.ui.gamesList.play.components.WinnerRow
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.Smooch14_line14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18

class GamePlayActivityScreen(
    val game: Game
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: GamePlayViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current
        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()


        LaunchedEffect(Unit) {
            viewModel.update(
                state.copy(
                    game = game
                )
            )
            viewModel.getAllPlayer()
            viewModel.setGameVariant()
        }

        LaunchedEffect(state.successEditAllPlayer) {
            if (state.successEditAllPlayer && state.successAddPlayGame && state.successAddHistoryGame) {
                navigator?.pop()
            }
        }

        BoardGameScaffold(
            titlePage = stringResource(R.string.history_add),
            selectedScreen = null,
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase
        ) { paddingValues ->
            GamePlayContent(
                state = state,
                update = viewModel::update,
                selectedPlayer = viewModel::selectedPlayer,
                addGamePlay = viewModel::validateAllData,
                paddingValues = paddingValues
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayContent(
    state: GamePlayState,
    update: (GamePlayState) -> Unit = {},
    selectedPlayer: (Player) -> Unit = {},
    addGamePlay: (Context) -> Unit = {},
    paddingValues: PaddingValues
) {
    val uriHandler = LocalUriHandler.current
    val calendarState = rememberSheetState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var playerSelectDialog by remember { mutableStateOf(false) }

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            update(
                state.copy(
                    playDate = date
                )
            )
        })


    Column(
        modifier = Modifier
            .padding(10.dp)
            .padding(bottom = 40.dp)
            .padding(paddingValues)
            .verticalScroll(scrollState),

            ) {
            GameInfo(state = state)

        }

        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(20.dp)
                    .height(150.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(15.dp)
                    ),
            ) {
                Text(
                    text = verticalText2,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = Smooch14_line14,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                        .clickable {
                            playerSelectDialog = true
                        }
                )
            }
            PlayerListToSelect(state = state, selectedPlayer = selectedPlayer)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(20.dp)
                    .height(150.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(15.dp)
                    ),
            ) {
                Text(
                    text = verticalText3,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = Smooch14_line14,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                )
            }
            Column {
                CooperateRow(state = state, update = update, Modifier.padding(start = 10.dp))

                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        shape = CutCornerShape(percent = 10),
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
                                stringResource(R.string.history_play_date),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = SmoochBold18
                            )
                            Text(
                                text = "${state.playDate}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = SmoochBold18
                            )
                        }
                    }
                }
                WinnerRow(state = state, update = update, Modifier.padding(start = 10.dp))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.history_description),
            style = Smooch18,
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            textStyle = Smooch18,
            value = state.descriptionGame,
            onValueChange = {
                update(
                    state.copy(
                        descriptionGame = it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .width(200.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            enabled = state.winner != "Who Win?",
            shape = CutCornerShape(percent = 10),
            onClick = {
                addGamePlay(context)
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
                Text(
                    stringResource(R.string.history_add),
                    style = SmoochBold18
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(10.dp),
        contentAlignment = Alignment.BottomCenter
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


    if (playerSelectDialog) {
        SelectPlayerDialog(
            state = state,
            selectedPlayer = selectedPlayer,
            update = update,
            onDismiss = {playerSelectDialog = false}
        )
    }

}

@Preview
@Composable
fun GamePlayActivityPreview() {
    BoardGameTheme {
        BoardGameScaffold(
            titlePage = stringResource(R.string.history_add),
            selectedScreen = null,
        ) { paddingValues ->
            val player =
                Player(
                    name = "Maksymilian Gosk WIelki Gracz",
                    winRatio = 2,
                    games = 6,
                    description = "ds",
                    selected = true
                )
            val player2 =
                Player(
                    name = "Kamila",
                    winRatio = 2,
                    games = 6,
                    description = "ds",
                    selected = false
                )

            val game = Game(
                name = "Nazwa Testowa",
                expansion = true,
                cooperate = true,
                baseGame = "Gra bazowa",
                minPlayer = "1",
                maxPlayer = "4",
                games = 6,
                id = "5456"
            )
            GamePlayContent(
                GamePlayState(
                    game,
                    playerList = listOf(player, player, player2, player2, player, player, player2)
                ),
                paddingValues = paddingValues
            )
        }
    }
}

@Preview
@Composable
fun GamePlayActivityPreviewWithoutPlayer() {
    BoardGameTheme {
        BoardGameScaffold(
            titlePage = stringResource(R.string.history_add),
            selectedScreen = null,
        ) { paddingValues ->

            val game = Game(
                name = "Nazwa Testowa Z Dwoma linijkami",
                expansion = true,
                cooperate = false,
                baseGame = "Gra bazowa",
                minPlayer = "1",
                maxPlayer = "4",
                games = 6,
                id = "5456"
            )
            GamePlayContent(
                GamePlayState(game, playerList = emptyList()),
                paddingValues = paddingValues
            )
        }
    }
}