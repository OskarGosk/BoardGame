package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

/** OUTLINED: zaznaczony ma ramkę (filtry). FILLED: zaznaczony jest wypełniony kolorem głównym (wybór wariantów). */
enum class AppSelectChipStyle { OUTLINED, FILLED }

/** Chip, który można zaznaczać i odznaczać. */
@Composable
fun AppSelectChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    style: AppSelectChipStyle = AppSelectChipStyle.OUTLINED,
) {
    val cs = MaterialTheme.colorScheme
    val filled = style == AppSelectChipStyle.FILLED
    val background = when {
        filled -> if (selected) cs.primary else cs.surfaceContainer
        else -> if (selected) cs.surfaceContainerLowest else cs.surfaceContainerLow
    }
    val borderColor = when {
        filled -> if (selected) Color.Transparent else cs.outlineVariant
        else -> if (selected) cs.primary else Color.Transparent
    }
    val textColor = when {
        filled -> if (selected) cs.onPrimary else cs.onSurfaceVariant
        else -> if (selected) cs.primary else cs.onSurfaceVariant
    }
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(background)
            .border(1.dp, borderColor, BoardGameShapes.Full)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (!filled && selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = textColor,
        )
    }
}

@Composable
private fun AppSelectChipPreviewContent() {
    var selected by remember { mutableStateOf(0) }
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All", "Base", "Expansions").forEachIndexed { index, label ->
            AppSelectChip(text = label, selected = selected == index, onToggle = { selected = index })
        }
    }
}

@Composable
private fun AppSelectChipFilledPreviewContent() {
    var on by remember { mutableStateOf(true) }
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSelectChip(text = "Rise of Ix", selected = on, onToggle = { on = !on }, style = AppSelectChipStyle.FILLED)
        AppSelectChip(text = "Epic Mode", selected = !on, onToggle = { on = !on }, style = AppSelectChipStyle.FILLED)
    }
}

@Preview(name = "Outlined · Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSelectChipLight() = BoardGameTheme(darkTheme = false) { AppSelectChipPreviewContent() }

@Preview(name = "Outlined · Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSelectChipDark() = BoardGameTheme(darkTheme = true) { AppSelectChipPreviewContent() }

@Preview(name = "Filled · Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSelectChipFilledLight() = BoardGameTheme(darkTheme = false) { AppSelectChipFilledPreviewContent() }

@Preview(name = "Filled · Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSelectChipFilledDark() = BoardGameTheme(darkTheme = true) { AppSelectChipFilledPreviewContent() }
