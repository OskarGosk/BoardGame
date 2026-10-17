package com.goskar.boardgame.ui.screens.gameDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.theme.BoardGameTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlow(
    modifier: Modifier = Modifier,
    items: List<String>,
    style: AppChipStyle,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { AppChip(text = it, style = style) }
    }
}


@Composable
private fun ChipFlowPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        ChipFlow(items = listOf("Strategy", "Economic", "Card Game"), style = AppChipStyle.CATEGORY)
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ChipFlowLight() = BoardGameTheme(darkTheme = false) { ChipFlowPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ChipFlowDark() = BoardGameTheme(darkTheme = true) { ChipFlowPreviewContent() }
