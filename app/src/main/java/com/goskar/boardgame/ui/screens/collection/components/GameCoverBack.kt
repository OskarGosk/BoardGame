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
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameUiState
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


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
        Spacer(Modifier.weight(1f))
        if (game.game.expansion) {
            Text(
                stringResource(R.string.board_expansion),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
        }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackIcon(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_add_session),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onAddSession
            )
            BackIcon(icon = Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit), tint = MaterialTheme.colorScheme.onSurfaceVariant, onClick = onEdit)
            if (onInfo != null) {
                BackIcon(icon = Icons.Default.Info, contentDescription = stringResource(R.string.cd_info), tint = MaterialTheme.colorScheme.onSurfaceVariant, onClick = onInfo)
            }
            BackIcon(icon = Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete), tint = MaterialTheme.colorScheme.error, onClick = onDelete)
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
