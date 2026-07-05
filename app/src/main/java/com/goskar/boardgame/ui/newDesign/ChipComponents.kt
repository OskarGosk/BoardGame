package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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


@Composable
fun BgChip(
    text: String,
    style: AppChipStyle = AppChipStyle.CATEGORY,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    val ext = appExt()
    val (bg, fg) = when (style) {
        AppChipStyle.CATEGORY -> cs.secondaryContainer to cs.onSecondaryContainer
        AppChipStyle.STATUS_WIN -> ext.successBg to ext.success
        AppChipStyle.STATUS_PLACE -> cs.surfaceContainerHigh to cs.onSurfaceVariant
        AppChipStyle.YEAR -> cs.surfaceContainerHigh to cs.onSurfaceVariant
        AppChipStyle.EXPANSION -> cs.primary.copy(alpha = 0.12f) to cs.primary
        AppChipStyle.BASE_GAME -> cs.secondaryContainer to cs.onSecondaryContainer
    }
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(bg)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = fg)
    }
}

@Composable
fun BgFilterChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) cs.surfaceContainerLowest else cs.surfaceContainerLow)
            .border(
                1.dp,
                if (selected) cs.primary else Color.Transparent,
                BoardGameShapes.Full
            )
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
            color = if (selected) cs.primary else cs.onSurfaceVariant,
        )
    }
}

@Composable
fun BgVariantChip(
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
            .border(
                1.dp,
                if (selected) Color.Transparent else cs.outlineVariant,
                BoardGameShapes.Full
            )
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) cs.onPrimary else cs.onSurfaceVariant,
        )
    }
}


@Preview(name = "Chips — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ChipsLightPreview() {
    BoardGameTheme(darkTheme = false) { ChipsPreviewContent() }
}

@Preview(name = "Chips — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ChipsDarkPreview() {
    BoardGameTheme(darkTheme = true) { ChipsPreviewContent() }
}

@Composable
private fun ChipsPreviewContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BgChip("STRATEGY", AppChipStyle.CATEGORY)
            BgChip("2018", AppChipStyle.YEAR)
            BgChip("WON", AppChipStyle.STATUS_WIN)
            BgChip("Base Game", AppChipStyle.BASE_GAME)
            BgChip("Expansion", AppChipStyle.EXPANSION)
        }
        var filter by remember { mutableStateOf(0) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Base Games", "Expansions").forEachIndexed { i, l ->
                BgFilterChip(l, filter == i, { filter = i })
            }
        }
        var v by remember { mutableStateOf(true) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BgVariantChip("Rise of Ix", v, { v = !v })
            BgVariantChip("Epic Mode", !v, { v = !v })
        }
    }
}
