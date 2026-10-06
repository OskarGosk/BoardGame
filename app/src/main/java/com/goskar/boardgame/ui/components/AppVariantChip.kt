package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppVariantChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) cs.primary else cs.surfaceContainer)
            .border(1.dp, if (selected) Color.Transparent else cs.outlineVariant, BoardGameShapes.Full)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) cs.onPrimary else cs.onSurfaceVariant,
        )
    }
}

@Composable
private fun AppVariantChipPreviewContent() {
    var on by remember { mutableStateOf(true) }
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppVariantChip("Rise of Ix", on, onToggle = { on = !on })
        AppVariantChip("Epic Mode", !on, onToggle = { on = !on })
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppVariantChipLight() =
    BoardGameTheme(darkTheme = false) { AppVariantChipPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppVariantChipDark() =
    BoardGameTheme(darkTheme = true) { AppVariantChipPreviewContent() }
