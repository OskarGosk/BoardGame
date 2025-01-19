package com.goskar.boardgame.ui.historyGames.lists.components

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
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleHistoryGame(
    modifier: Modifier
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
                .weight(4f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "2025-01-21",
                style = Smooch16)
            Text("Marvel",
                style = Smooch16)
        }

        Text("Oskar",
            style = SmoochBold26,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        )
    }
}

@Preview
@Composable
fun SingleHistoryGamePreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(0.dp)) {
            Column {
                SingleHistoryGame(modifier = Modifier)
                SingleHistoryGame(modifier = Modifier)
            }
        }
    }
}
