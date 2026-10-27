package com.goskar.boardgame.ui.screens.logGameplay.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.ui.components.labelRes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun GameModeSelector(
    modifier: Modifier = Modifier,
    modes: List<GameMode>,
    selected: GameMode,
    onSelect: (GameMode) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = stringResource(mode.labelRes),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun GameModeSelectorPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        GameModeSelector(
            modes = GameMode.entries,
            selected = GameMode.TEAM,
            onSelect = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameModeSelectorLight() = BoardGameTheme(darkTheme = false) { GameModeSelectorPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameModeSelectorDark() = BoardGameTheme(darkTheme = true) { GameModeSelectorPreviewContent() }

@Preview(name = "Large font", showBackground = true, backgroundColor = 0xFFF7F9FF, fontScale = 2f)
@Composable
private fun GameModeSelectorLargeFont() = BoardGameTheme(darkTheme = false) { GameModeSelectorPreviewContent() }
