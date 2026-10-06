package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

private val CoverImagePadding = 8.dp
private val PlaceholderIconSize = 28.dp

/**
 * Game cover thumbnail. Shows [uri] when it is not blank, otherwise a neutral placeholder,
 * so the box keeps its size and layout while the game has no cover.
 */
@Composable
fun AppGameCover(
    modifier: Modifier = Modifier,
    uri: String?,
    size: Dp = 56.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(BoardGameShapes.Medium)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        if (uri.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(PlaceholderIconSize),
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(uri.toUri()).build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(CoverImagePadding),
            )
        }
    }
}

@Preview(name = "No cover — Light", showBackground = true)
@Composable
private fun AppGameCoverEmptyLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) { AppGameCover(uri = null) }
    }
}

@Preview(name = "No cover — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppGameCoverEmptyDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Box(Modifier.padding(16.dp)) { AppGameCover(uri = "") }
    }
}

@Preview(name = "Large — Light", showBackground = true)
@Composable
private fun AppGameCoverLargePreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) { AppGameCover(uri = null, size = 72.dp) }
    }
}
