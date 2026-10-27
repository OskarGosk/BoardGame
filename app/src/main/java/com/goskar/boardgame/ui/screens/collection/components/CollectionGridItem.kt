package com.goskar.boardgame.ui.screens.collection.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameUiState
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun CollectionGridItem(
    modifier: Modifier = Modifier,
    game: GameUiState,
    onInfo: (() -> Unit)? = null,
    onAddSession: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val hasCover = !(game.game.uriFromBgg ?: game.game.uri).isNullOrBlank()
    // no cover -> start on the info side so the tile is never an empty box
    var flipped by remember { mutableStateOf(!hasCover) }
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, label = "flip")

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable { flipped = !flipped },
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                GameCoverFront(game = game)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }) {
                    GameCoverBack(game = game, onInfo = onInfo, onAddSession = onAddSession, onEdit = onEdit, onDelete = onDelete)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = game.game.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
private fun CollectionGridItemPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        CollectionGridItem(
            modifier = Modifier.size(width = 160.dp, height = 200.dp),
            game = PreviewGame,
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CollectionGridItemLight() = BoardGameTheme(darkTheme = false) { CollectionGridItemPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CollectionGridItemDark() = BoardGameTheme(darkTheme = true) { CollectionGridItemPreviewContent() }
