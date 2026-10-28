package com.goskar.boardgame.ui.screens.addGame.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.ui.components.AppSelectChip
import com.goskar.boardgame.ui.components.AppSelectChipStyle
import com.goskar.boardgame.ui.components.labelRes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun GameModesField(
    modifier: Modifier = Modifier,
    selected: Set<GameMode>,
    onToggle: (GameMode) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.addgame_modes_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.addgame_modes_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GameMode.entries.forEach { mode ->
                AppSelectChip(
                    style = AppSelectChipStyle.FILLED,
                    text = stringResource(mode.labelRes),
                    selected = mode in selected,
                    onToggle = { onToggle(mode) },
                )
            }
        }
    }
}

@Composable
private fun GameModesFieldPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        GameModesField(selected = setOf(GameMode.COMPETITIVE, GameMode.COOPERATIVE), onToggle = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameModesFieldLight() = BoardGameTheme(darkTheme = false) { GameModesFieldPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameModesFieldDark() = BoardGameTheme(darkTheme = true) { GameModesFieldPreviewContent() }

@Preview(name = "Large font", showBackground = true, backgroundColor = 0xFFF7F9FF, fontScale = 2f)
@Composable
private fun GameModesFieldLargeFont() = BoardGameTheme(darkTheme = false) { GameModesFieldPreviewContent() }
