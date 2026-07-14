package com.goskar.boardgame.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badge: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
        }
        if (badge != null) {
            Text(badge.uppercase(), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
        if (action != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = cs.primary,
                modifier = Modifier
                    .then(if (onAction != null) Modifier.clickable { onAction() } else Modifier)
                    .padding(start = 8.dp),
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = cs.primary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AppSectionHeaderPreviewContent() {
    Column(Modifier.padding(16.dp)) {
        AppSectionHeader("Recent Sessions", action = "View All", onAction = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSectionHeaderLight() =
    BoardGameTheme(darkTheme = false) { AppSectionHeaderPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSectionHeaderDark() =
    BoardGameTheme(darkTheme = true) { AppSectionHeaderPreviewContent() }
