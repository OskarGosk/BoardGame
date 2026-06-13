package com.goskar.boardgame.ui.gamesHistory.lists.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold16
import com.goskar.boardgame.ui.theme.SmoochBold26
import java.time.LocalDate

@Composable
fun SingleHistoryGame(
    modifier: Modifier,
    historyGame: HistoryGameWithExpansion
) {
    var isExpended by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .clickable { isExpended = !isExpended },
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .sizeIn(maxHeight = 90.dp, minHeight = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1.5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = historyGame.history.gameData.toString(),
                    style = Smooch16
                )
                Text(
                    text = historyGame.history.gameName,
                    maxLines = if (isExpended) 2 else 1,
                    style = Smooch16,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = historyGame.history.winner,
                style = SmoochBold26,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
            )
        }
        if (isExpended) {
            var listOfPlayer = ""
            historyGame.history.listOfPlayer.forEach { listOfPlayer = listOfPlayer + it + ", " }
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_players_list) + " ",
                    style = SmoochBold16
                )
                Text(
                    text = listOfPlayer,
                    style = Smooch16
                )
            }

            if (!historyGame.expansion.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.history_expansion_list) + " ",
                        style = SmoochBold16
                    )
                    Text(
                        text = historyGame.expansion.joinToString("\n") { it.expansionName },
                        style = Smooch16
                    )
                }
            }
            if (historyGame.history.description.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.history_description) + " ",
                        style = SmoochBold16
                    )
                    Text(
                        text = historyGame.history.description,
                        style = Smooch16
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun SingleHistoryGamePreview() {
    val history1 = HistoryGameWithExpansion(
        HistoryGame(
            gameName = "Marvel Z bardzo długą nazwą aby tyrochę to rozdzielić",
            winner = "Maksymilianjusz",
            gameData = LocalDate.parse("2025-01-23"),
            listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
            description = "Jasne, żę tak było ",
            id = "dsa"
        ), emptyList()
    )

    val history2 = HistoryGameWithExpansion(
        HistoryGame(
            gameName = "Scrable",
            winner = "Kamila",
            gameData = LocalDate.parse("2025-01-01"),
            listOfPlayer = listOf(
                "Oskar",
                "Kamila",
                "Maksymilian",
                "Marzena",
                "Magdalena",
                "Korneliusz",
                "KtośTamn"
            ),
            description = "",
            id = "dsa"
        ), emptyList()
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
