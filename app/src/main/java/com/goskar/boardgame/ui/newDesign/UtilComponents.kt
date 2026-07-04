package com.goskar.boardgame.ui.newDesign

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.*

// =============================================================================
// UTIL COMPONENTS — theme-aware (single MaterialTheme-based set)
// =============================================================================

/**
 * Section heading row — title (+ optional subtitle) + optional badge + optional "View All" action.
 */
@Composable
fun BgSectionHeader(
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

/**
 * Empty state — icon + heading + optional body + optional CTA.
 */
@Composable
fun BgEmptyState(
    icon: ImageVector,
    heading: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = BoardGameSpacing.Xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = cs.outlineVariant, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            heading,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = cs.onSurfaceVariant, textAlign = TextAlign.Center
        )
        if (body != null) {
            Spacer(Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = cs.outline, textAlign = TextAlign.Center)
        }
        if (action != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            BgSecondaryButton(action, onAction, modifier = Modifier.fillMaxWidth(0.55f))
        }
    }
}

/**
 * Progress bar — primary fill on a subtle track.
 */
@Composable
fun BgProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    fillColor: Color = MaterialTheme.colorScheme.primary,
) {
    Box(modifier = modifier.fillMaxWidth().height(height).clip(BoardGameShapes.Full).background(trackColor)) {
        Box(
            modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight()
                .clip(BoardGameShapes.Full).background(fillColor)
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Utils — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun UtilsLightPreview() {
    BoardGameTheme(darkTheme = false) { UtilsPreviewContent() }
}

@Preview(name = "Utils — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun UtilsDarkPreview() {
    BoardGameTheme(darkTheme = true) { UtilsPreviewContent() }
}

@Composable
private fun UtilsPreviewContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BgSectionHeader("Recent Sessions", action = "View All", onAction = {})
        BgSectionHeader("Today", badge = "2 SESSIONS")
        BgProgressBar(progress = 0.64f)
        BgEmptyState(
            Icons.Default.Lock, "No older history found",
            body = "Your past sessions will appear here"
        )
    }
}
