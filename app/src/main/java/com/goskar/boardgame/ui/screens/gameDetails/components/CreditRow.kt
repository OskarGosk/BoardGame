package com.goskar.boardgame.ui.screens.gameDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun CreditRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


@Composable
private fun CreditRowPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        CreditRow(label = "Designer", value = "Paul Dennen")
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CreditRowLight() = BoardGameTheme(darkTheme = false) { CreditRowPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CreditRowDark() = BoardGameTheme(darkTheme = true) { CreditRowPreviewContent() }
