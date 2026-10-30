package com.goskar.boardgame.ui.screens.history.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.components.AppSelectChip
import com.goskar.boardgame.ui.screens.history.viewmodel.HistoryFilter
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun HistoryFilterChips(
    modifier: Modifier = Modifier,
    filters: List<HistoryFilter>,
    selected: HistoryFilter,
    onSelect: (HistoryFilter) -> Unit,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        filters.forEach { filter ->
            AppSelectChip(
                text = stringResource(filter.label),
                selected = filter == selected,
                onToggle = { onSelect(filter) },
            )
        }
    }
}


@Composable
private fun HistoryFilterChipsPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        HistoryFilterChips(filters = HistoryFilter.entries, selected = HistoryFilter.COOP, onSelect = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun HistoryFilterChipsLight() = BoardGameTheme(darkTheme = false) { HistoryFilterChipsPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HistoryFilterChipsDark() = BoardGameTheme(darkTheme = true) { HistoryFilterChipsPreviewContent() }
