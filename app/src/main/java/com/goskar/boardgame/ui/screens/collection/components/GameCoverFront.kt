package com.goskar.boardgame.ui.screens.collection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameUiState
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun GameCoverFront(
    modifier: Modifier = Modifier,
    game: GameUiState,
) {
    val gameUri = game.game.uriFromBgg ?: game.game.uri
    if (!gameUri.isNullOrBlank()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(gameUri.toUri()).build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(BoardGameShapes.Small)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = pluralStringResource(R.plurals.collection_plays, game.game.games, game.game.games),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = Color.White
                )
            }
        }
    } else {
        // no cover: show basic info instead of an empty box
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            val players = playersLabel(game)
            if (players.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    players,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                pluralStringResource(R.plurals.collection_plays, game.game.games, game.game.games),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun playersLabel(game: GameUiState): String {
    val min = game.game.minPlayer.trim()
    val max = game.game.maxPlayer.trim()
    return when {
        min.isBlank() && max.isBlank() -> ""
        min.isBlank() -> stringResource(R.string.collection_players_count, max)
        max.isBlank() || min == max -> stringResource(R.string.collection_players_count, min)
        else -> stringResource(R.string.collection_players_range, min, max)
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
private fun GameCoverFrontPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        Box(Modifier.size(width = 160.dp, height = 200.dp)) { GameCoverFront(game = PreviewGame) }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameCoverFrontLight() = BoardGameTheme(darkTheme = false) { GameCoverFrontPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameCoverFrontDark() = BoardGameTheme(darkTheme = true) { GameCoverFrontPreviewContent() }
