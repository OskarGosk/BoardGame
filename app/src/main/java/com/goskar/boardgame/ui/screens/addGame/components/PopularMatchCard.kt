package com.goskar.boardgame.ui.screens.addGame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.screens.addGame.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun PopularMatchCard(
    modifier: Modifier = Modifier,
    match: PopularMatch,
    onAdd: (() -> Unit)?,
    onOpen: () -> Unit = {},
) {
    AppListCard(modifier = modifier, onClick = onOpen) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(match.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppChip(text = match.category, style = AppChipStyle.CATEGORY)
                    AppChip(text = match.year, style = AppChipStyle.YEAR)
                }
            }
            if (onAdd != null) {
                IconButton(onClick = onAdd) {
                    Icon(
                        Icons.Default.AddCircleOutline,
                        contentDescription = stringResource(R.string.cd_add_match, match.name),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}


@Composable
private fun PopularMatchCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        PopularMatchCard(
            match = PopularMatch(id = "1", name = "Dune: Imperium", category = "Strategy", year = "2020"),
            onAdd = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun PopularMatchCardLight() = BoardGameTheme(darkTheme = false) { PopularMatchCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PopularMatchCardDark() = BoardGameTheme(darkTheme = true) { PopularMatchCardPreviewContent() }
