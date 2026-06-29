package com.goskar.boardgame.ui.newDesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
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
// UTIL COMPONENTS — Light
// =============================================================================

/**
 * Section heading row — title + optional badge count + "View All" link.
 * Matches "Recent Sessions  VIEW ALL", "Today  2 SESSIONS".
 */
@Composable
fun BgSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = BoardGameTypography.TitleLg, color = BoardGameColors.OnSurface,
            modifier = Modifier.weight(1f))
        if (badge != null) {
            Text(badge.uppercase(), style = BoardGameTypography.LabelCaps, color = BoardGameColors.OnSurfaceVariant)
        }
        if (action != null) {
            Text(
                text     = action,
                style    = BoardGameTypography.BodySm.copy(fontWeight = FontWeight.SemiBold),
                color    = BoardGameColors.Primary,
                modifier = Modifier
                    .then(if (onAction != null) Modifier.clickable { onAction() } else Modifier)
                    .padding(start = 8.dp),
            )
        }
    }
}

/**
 * Empty state — icon + heading + optional body + optional CTA.
 * Matches "No older history found" on History screen.
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
    Column(
        modifier            = modifier.fillMaxWidth().padding(vertical = BoardGameSpacing.Xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = BoardGameColors.OutlineVariant, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(heading, style = BoardGameTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
            color = BoardGameColors.OnSurfaceVariant, textAlign = TextAlign.Center)
        if (body != null) {
            Spacer(Modifier.height(4.dp))
            Text(body, style = BoardGameTypography.BodySm, color = BoardGameColors.Outline, textAlign = TextAlign.Center)
        }
        if (action != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            BgSecondaryButton(
                action,
                onAction,
                modifier = Modifier.fillMaxWidth(0.55f)
            )
        }
    }
}

/**
 * Progress bar — terracotta fill on light gray track.
 * Matches Win Ratio bar on Home / Profile screens.
 */
@Composable
fun BgProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color = BoardGameColors.SurfaceContainerHigh,
    fillColor: Color = BoardGameColors.Primary,
) {
    Box(modifier = modifier.fillMaxWidth().height(height).clip(BoardGameShapes.Full).background(trackColor)) {
        Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight()
            .clip(BoardGameShapes.Full).background(fillColor))
    }
}

// =============================================================================
// UTIL COMPONENTS — Dark
// =============================================================================

/**
 * Dark section header — "Recent Sessions  View All History".
 */
@Composable
fun BgDarkSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = BoardGameDarkTypography.TitleLg, color = BoardGameDarkColors.OnSurface)
            if (subtitle != null)
                Text(subtitle, style = BoardGameDarkTypography.BodyMd, color = BoardGameDarkColors.OnSurfaceVariant)
        }
        if (action != null) {
            Text(
                text     = action,
                style    = BoardGameDarkTypography.BodyMd.copy(fontWeight = FontWeight.SemiBold),
                color    = BoardGameDarkColors.Primary,
                modifier = Modifier
                    .then(if (onAction != null) Modifier.clickable { onAction() } else Modifier)
                    .padding(start = 8.dp),
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = BoardGameDarkColors.Primary, modifier = Modifier.size(16.dp))
        }
    }
}

/**
 * Dark empty state — dashed-border card with icon circle.
 * Matches "Add New Game — Scan barcode or search database" on dark Collection.
 */
@Composable
fun BgDarkEmptyState(
    icon: ImageVector,
    heading: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    dashed: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxWidth().clip(BoardGameShapes.ExtraLarge)
            .background(BoardGameDarkColors.SurfaceContainer)
            .then(if (dashed) Modifier.border(1.dp, BoardGameDarkColors.OutlineVariant, BoardGameShapes.ExtraLarge) else Modifier)
            .padding(vertical = 40.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(BoardGameDarkColors.SecondaryContainer),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = BoardGameDarkColors.Primary, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(heading, style = BoardGameDarkTypography.TitleLg, color = BoardGameDarkColors.OnSurface,
                textAlign = TextAlign.Center)
            if (body != null) {
                Spacer(Modifier.height(6.dp))
                Text(body, style = BoardGameDarkTypography.BodyMd, color = BoardGameDarkColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center)
            }
            if (action != null && onAction != null) {
                Spacer(Modifier.height(20.dp))
                BgDarkPrimaryButton(
                    action,
                    onAction,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }
    }
}

/**
 * Dark progress bar — orange fill on dark track.
 */
@Composable
fun BgDarkProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color = BoardGameDarkColors.SurfaceContainerHigh,
    fillColor: Color = BoardGameDarkColors.OrangeFill,
) {
    Box(modifier = modifier.fillMaxWidth().height(height).clip(BoardGameShapes.Full).background(trackColor)) {
        Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight()
            .clip(BoardGameShapes.Full).background(fillColor))
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Utils — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun UtilsLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BgSectionHeader(
                "Recent Sessions",
                badge = "3 sessions",
                action = "View All",
                onAction = {})
            BgSectionHeader("Today", badge = "2 SESSIONS")
            BgProgressBar(progress = 0.64f)
            BgEmptyState(
                Icons.Default.Lock, "No older history found",
                body = "Your past sessions will appear here"
            )
        }
    }
}

@Preview(name = "Utils — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun UtilsDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BgDarkSectionHeader(
                "Recent Sessions", subtitle = "Your collection is waiting.",
                action = "View All History", onAction = {})
            BgDarkProgressBar(progress = 0.64f)
            BgDarkEmptyState(
                Icons.Default.Add, "Add New Game",
                body = "Scan barcode or search database", action = "Add Game", onAction = {})
        }
    }
}
