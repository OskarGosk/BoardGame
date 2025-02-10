package com.goskar.boardgame.ui.gamesHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.gamesHistory.lists.components.GamesHistorySearchRow
import com.goskar.boardgame.ui.gamesHistory.lists.HistoryGamesList
import org.koin.androidx.compose.koinViewModel

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HistoryGameListContent(
    state: GamesHistoryState,
    update: (GamesHistoryState) -> Unit = {}
) {
    BoardGameScaffold(
        titlePage = R.string.history_game_screen,
        selectedScreen= BottomBarElements.HistoryListButton.title
    ) { paddingValues ->



        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            GamesHistorySearchRow(state = state, update = update)
            HistoryGamesList(state)
        }
    }
}

@Preview
@Composable
fun HistoryGameListContentPreview() {
    val history1 = HistoryGame(
        gameName = "Marvel",
        winner = "Oskar",
        gameData = "2025-01-23",
        listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
        description = "",
        id = "dsa"
    )

    val history2 = HistoryGame(
        gameName = "Scrable",
        winner = "Kamila",
        gameData = "2025-01-01",
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