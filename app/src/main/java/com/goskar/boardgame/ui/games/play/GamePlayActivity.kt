package com.goskar.boardgame.ui.games.play

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.Player
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.games.play.components.GameInfo
import com.goskar.boardgame.ui.games.play.components.PlayerListToSelect
import com.goskar.boardgame.ui.games.play.components.WinnerRow
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

        LaunchedEffect(Unit) {
            viewModel.update(
                state.copy(
                    game = game
                )
            )
            viewModel.getAllPlayer()
        }

        LaunchedEffect(state.successEditAllPlayer) {
            if (state.successEditAllPlayer && state.successAddPlayGame && state.successAddHistoryGame) {
                navigator?.pop()
            }
        }

        BoardGameTheme {
            GamePlayContent(
                state = state,
                update = viewModel::update,
                selectedPlayer = viewModel::selectedPlayer,
                addGamePlay = viewModel::validateAllData,
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
    addGamePlay: () -> Unit = {},
) {
    val calendarState = rememberSheetState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

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

    BoardGameScaffold(
        titlePage = stringResource(R.string.board_list)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(10.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState),

            ) {
//            AnimatedVisibility(visible = visible) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(4.dp))
//                        .background(Color.Gray)
//                        .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                        .zIndex(100f),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Error",
//                    )
            //DO PRZEROBIENIA NA POTRZEBY APKI
//                }
//        AppAlert(
//            modifier = Modifier,
//            text = state.successMessage.asString(),
//            type = AlertType.SUCCESS,
//        )
//            }
            GameInfo(state = state)
            PlayerListToSelect(state = state, selectedPlayer = selectedPlayer)

            Row(
                modifier = Modifier
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
                            stringResource(R.string.play_date),
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
            WinnerRow(state = state, update = update)

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Game Description:",
                style = Smooch18,
                modifier = Modifier
                    .fillMaxWidth()
            )
            var descriptionGame by remember { mutableStateOf("") }
            OutlinedTextField(
                textStyle = Smooch18,
                value = descriptionGame,
                onValueChange = { descriptionGame = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(200.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                enabled = state.winner != "Who Win?",
                shape = CutCornerShape(percent = 10),
                onClick = {
                    addGamePlay()
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
                        stringResource(R.string.add_game_to_history),
                        style = SmoochBold18)
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
            Player(name = "Maksymilian Gosk WIelki Gracz", winRatio = 2, games = 6, description = "ds", selected = true)
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
        GamePlayContent(
            GamePlayState(
                game,
                playerList = listOf(player, player, player2, player2, player, player, player2)
            )
        )
    }
}

@Preview
@Composable
fun GamePlayActivityPreviewWithoutPlayer() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        val game = Game(
            name = "Nazwa Testowa",
            expansion = true,
            baseGame = "Gra bazowa",
            minPlayer = "1",
            maxPlayer = "4",
            games = 6,
            id = "5456"
        )
        GamePlayContent(GamePlayState(game, playerList = emptyList()))
    }
}