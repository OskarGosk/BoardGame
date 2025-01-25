package com.goskar.boardgame.ui.gamesHistory.lists.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleHistoryGame(
    modifier: Modifier,
    historyGame : HistoryGame
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp)
            .border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = historyGame.gameData,
                style = Smooch16)
            Text(historyGame.gameName,
                style = Smooch16)
        }

        Text(text = historyGame.winner,
            style = SmoochBold26,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        )
    }
}

@Preview
@Composable
fun SingleHistoryGamePreview() {
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
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(0.dp)) {
            Column {
                SingleHistoryGame(modifier = Modifier, historyGame = history1)
                SingleHistoryGame(modifier = Modifier, historyGame = history2)
            }
        }
    }
}
