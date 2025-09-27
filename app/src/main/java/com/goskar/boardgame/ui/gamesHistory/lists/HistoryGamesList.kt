package com.goskar.boardgame.ui.gamesHistory.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryState
import com.goskar.boardgame.ui.gamesHistory.lists.components.SingleHistoryGame
import java.time.LocalDate

@Composable
fun HistoryGamesList(state: GamesHistoryState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 10.dp)

    ) {
        val newHistoryList: List<HistoryGameWithExpansion> = when (state.sortOption) {
            R.string.default_sort -> state.historyGameWithExpansion
            R.string.name_ascending -> state.historyGameWithExpansion.sortedBy { it.history.winner }
            R.string.name_descending -> state.historyGameWithExpansion.sortedByDescending { it.history.winner }
            R.string.played_ascending -> state.historyGameWithExpansion.sortedBy { it.history.gameName }
            R.string.played_descending -> state.historyGameWithExpansion.sortedByDescending { it.history.gameName }
            else -> state.historyGameWithExpansion
        }
        newHistoryList.asReversed().forEach {
            if (it.history.gameName.lowercase()
                    .contains(state.searchTxt.lowercase()) || it.history.winner.lowercase()
                    .contains(state.searchTxt.lowercase())
            )
                SingleHistoryGame(Modifier, it)
        }
    }
}

@Preview
@Composable
fun HistoryGamesListPreview() {

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
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier) {
            HistoryGamesList(
                GamesHistoryState(
                    historyList = listOf(history1, history2, history1, history1)
                )
            )
        }
    }
}