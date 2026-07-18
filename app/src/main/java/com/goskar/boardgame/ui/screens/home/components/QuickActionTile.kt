package com.goskar.boardgame.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun QuickActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun QuickActionTileLightPreview() {
    BoardGameTheme(darkTheme = false) {
        QuickActionTile(
            icon = Icons.Default.Add,
            label = "Add Game",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun QuickActionTileDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        QuickActionTile(
            icon = Icons.Default.Add,
            label = "Add Game",
            modifier = Modifier.padding(16.dp)
        )
    }
}
