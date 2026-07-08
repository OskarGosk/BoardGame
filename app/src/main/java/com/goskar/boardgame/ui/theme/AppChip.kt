package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement

@Composable
fun AppChip(
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
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = fg)
    }
}

@Composable
private fun AppChipPreviewContent() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppChip("STRATEGY", AppChipStyle.CATEGORY)
        AppChip("WON", AppChipStyle.STATUS_WIN)
        AppChip("2018", AppChipStyle.YEAR)
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppChipLight() = BoardGameTheme(darkTheme = false) { AppChipPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppChipDark() = BoardGameTheme(darkTheme = true) { AppChipPreviewContent() }
