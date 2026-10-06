package com.goskar.boardgame.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.ui.theme.BoardGameElevation
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    icon: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = BoardGameShapes.Large,
        elevation = CardDefaults.cardElevation(BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(value, style = MaterialTheme.typography.displayMedium, color = valueColor)
            }
            icon?.invoke()
        }
    }
}

@Composable
private fun AppStatCardPreviewContent() {
    Column(Modifier.padding(16.dp)) {
        AppStatCard("Total Games", "142")
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppStatCardLight() = BoardGameTheme(darkTheme = false) { AppStatCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppStatCardDark() = BoardGameTheme(darkTheme = true) { AppStatCardPreviewContent() }
