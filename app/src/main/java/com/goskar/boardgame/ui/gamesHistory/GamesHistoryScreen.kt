package com.goskar.boardgame.ui.gamesHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.ui.components.other.EmptyListWithButton
import com.goskar.boardgame.ui.components.other.SearchRowGlobal
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.gamesHistory.lists.HistoryGamesList
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

class HistoryGameListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel :GamesHistoryViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        HistoryGameListContent(
            state = state,
            update = viewModel::update
        )
    }
}

@Composable
fun HistoryGameListContent(
    state: GamesHistoryState,
    update: (GamesHistoryState) -> Unit = {}
) {
    val navigator = LocalNavigator.current
    BoardGameScaffold(
        titlePage = stringResource(R.string.history_game_screen),
        selectedScreen= BottomBarElements.HistoryListButton.title
    ) { paddingValues ->



        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            if (state.historyList.isEmpty()) {
                EmptyListWithButton(
                    headerText = R.string.history_empty_list,
                    infoText = R.string.history_empty_list_add,
                    buttonText = R.string.board_add,
                    onClick = {
                        navigator?.push(GameListScreen())
                    }
                )
            }
            else {
                SearchRowGlobal(
                    searchHelp = R.string.player_name,
                    searchTxt = state.searchTxt,
                    sortOption = state.sortOption,
                    updateTxt = {
                        update(
                            state.copy(
                                searchTxt = it
                            )
                        )
                    },
                    clearTxt = {
                        update(
                            state.copy(
                                searchTxt = ""
                            )
                        )
                    },
                    updateSort = {
                        update(
                            state.copy(
                                sortOption = it
                            )
                        )
                    }
                )
                HistoryGamesList(state)
            }
        }
    }
}

@Preview
@Composable
fun HistoryGameListContentPreview() {
    val history1 = HistoryGame(
        gameName = "Marvel",
        winner = "Oskar",
        gameData = LocalDate.parse("2025-01-23"),
        listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
        description = "",
        id = "dsa"
    )

    val history2 = HistoryGame(
        gameName = "Scrable",
        winner = "Kamila",
        gameData = LocalDate.parse("2025-01-01"),
        listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
        description = "",
        id = "dsa"
    )
    Surface(
    ) {
        HistoryGameListContent(
            state = GamesHistoryState(
                historyList = listOf(history1, history2, history1, history2)
            )
        )
    }
}

@Preview
@Composable
fun HistoryGameListEmptyContentPreview() {

    Surface(
    ) {
        HistoryGameListContent(
            state = GamesHistoryState(
                historyList = emptyList()
            )
        )
    }
}