package com.goskar.boardgame.ui.screens.collection.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameUiState
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.ui.theme.BoardGameTheme
import java.util.Locale


@Composable
fun GameCoverBack(
    modifier: Modifier = Modifier,
    game: GameUiState,
    onInfo: (() -> Unit)?,
    onAddSession: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (game.game.expansion) {
            Text(
                stringResource(R.string.board_expansion),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
        }
        GameMeta(game = game)
        Spacer(Modifier.weight(1f))
        InfoLine(
            label = stringResource(R.string.collection_min_players),
            value = game.game.minPlayer.ifBlank { "—" })
        Spacer(Modifier.height(8.dp))
        InfoLine(
            label = stringResource(R.string.collection_max_players),
            value = game.game.maxPlayer.ifBlank { "—" })
        Spacer(Modifier.height(8.dp))
        InfoLine(label = stringResource(R.string.collection_games_played), value = game.game.games.toString())
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackIcon(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_add_session),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onAddSession,
                modifier = Modifier.weight(1f),
            )
            BackIcon(
                icon = Icons.Default.Edit,
                contentDescription = stringResource(R.string.cd_edit),
                onClick = onEdit,
                modifier = Modifier.weight(1f),
            )
            if (onInfo != null) {
                BackIcon(
                    icon = Icons.Default.Info,
                    contentDescription = stringResource(R.string.cd_info),
                    onClick = onInfo,
                    modifier = Modifier.weight(1f),
                )
            }
            BackIcon(
                icon = Icons.Default.Delete,
                contentDescription = stringResource(R.string.cd_delete),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = onDelete,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GameMeta(game: GameUiState) {
    val category = game.game.category
    val year = game.game.yearPublished
    val rating = game.game.rating
    if (category.isNullOrBlank() && year == null && rating == null) return
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        if (!category.isNullOrBlank()) {
            AppChip(text = category, style = AppChipStyle.CATEGORY)
        }
        if (year != null) {
            AppChip(text = year.toString(), style = AppChipStyle.YEAR)
        }
        if (rating != null) {
            Text(
                text = "★ " + String.format(Locale.US, "%.1f", rating),
                style = MaterialTheme.typography.labelSmall,
                color = appExt().success,
                maxLines = 1
            )
        }
    }
}


private val PreviewGame = GameUiState(
    game = Game(
        name = "Dune: Imperium",
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 12,
        category = "Strategy",
        yearPublished = 2020,
        rating = 8.4,
    ),
)

@Composable
private fun GameCoverBackPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        Box(Modifier.size(width = 160.dp, height = 200.dp)) {
            GameCoverBack(
                game = PreviewGame,
                onInfo = {},
                onAddSession = {},
                onEdit = {},
                onDelete = {},
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameCoverBackLight() = BoardGameTheme(darkTheme = false) { GameCoverBackPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameCoverBackDark() = BoardGameTheme(darkTheme = true) { GameCoverBackPreviewContent() }
