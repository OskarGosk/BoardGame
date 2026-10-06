package com.goskar.boardgame.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import com.goskar.boardgame.ui.theme.BoardGameElevation
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = BoardGameShapes.Large,
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation = CardDefaults.cardElevation(defaultElevation = BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun AppListCardPreviewContent() {
    Column(Modifier.padding(16.dp)) {
        AppListCard { Text("Everdell", color = MaterialTheme.colorScheme.onSurface) }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppListCardLight() = BoardGameTheme(darkTheme = false) { AppListCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppListCardDark() = BoardGameTheme(darkTheme = true) { AppListCardPreviewContent() }
