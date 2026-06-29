package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =============================================================================
// CARDS — Light
// =============================================================================

/**
 * Standard list card — white bg, subtle border, 16dp radius.
 * Matches "Everdell", "Wingspan" game list rows.
 */
@Composable
fun BgListCard(
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

/**
 * Hero / featured card — full-width image + text below.
 * Matches "Terraforming Mars" large card on Collection screen.
 */
@Composable
fun BgHeroCard(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeStyle: BgChipStyle = BgChipStyle.BASE_GAME,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    imageContent: @Composable BoxScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = BoardGameShapes.ExtraLarge,
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation = CardDefaults.cardElevation(BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = BoardGameColors.SurfaceContainerLowest),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(
                        androidx.compose.foundation.shape.RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
            ) {
                imageContent()
                if (badge != null) {
                    BgChip(
                        badge,
                        badgeStyle,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(title, style = BoardGameTypography.TitleLg, color = BoardGameColors.OnSurface)
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        subtitle,
                        style = BoardGameTypography.BodySm,
                        color = BoardGameColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Stat / metric card — label + large number.
 * Matches "TOTAL GAMES 142", "WIN RATIO 64%" on Home screen.
 */
@Composable
fun BgStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = BoardGameColors.Primary,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = BoardGameShapes.Large,
        elevation = CardDefaults.cardElevation(BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = BoardGameColors.SurfaceContainerLowest),
        border = BorderStroke(0.5.dp, BoardGameColors.OutlineVariant.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    label.uppercase(),
                    style = BoardGameTypography.LabelCaps,
                    color = BoardGameColors.OnSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(value, style = BoardGameTypography.DisplayLgMobile, color = valueColor)
            }
            trailingContent?.invoke()
        }
    }
}

/**
 * Settings row — icon tile + title + subtitle + trailing arrow.
 * Matches "Account Settings", "Data Synchronization" on Profile screen.
 */
@Composable
fun BgSettingsRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    iconBg: Color = BoardGameColors.SurfaceContainerLow,
    subtitle: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(BoardGameShapes.Small)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = BoardGameColors.Primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = BoardGameTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
                color = BoardGameColors.OnSurface
            )
            if (subtitle != null) Text(
                subtitle,
                style = BoardGameTypography.BodySm,
                color = BoardGameColors.OnSurfaceVariant
            )
        }
        trailingContent?.invoke()
    }
}

// =============================================================================
// CARDS — Dark
// =============================================================================

/**
 * Dark list card — #201F1F bg, 1px #333 border, 24dp radius.
 * Matches game rows on dark Add Game / Collection screens.
 */
@Composable
fun BgDarkListCard(
    modifier: Modifier = Modifier,
    shape: Shape = BoardGameShapes.ExtraLarge,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        onClick = onClick ?: {},
        enabled = onClick != null,
        elevation = CardDefaults.cardElevation(BoardGameDarkElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = BoardGameDarkColors.SurfaceContainer),
        border = BorderStroke(1.dp, BoardGameDarkColors.CardBorder),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/**
 * Dark hero card — full-bleed image with gradient overlay.
 * Matches "Ark Nova" featured card and session cards on dark screens.
 */
@Composable
fun BgDarkHeroCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    chips: @Composable (RowScope.() -> Unit)? = null,
    trailingButton: @Composable (BoxScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    imageContent: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(BoardGameShapes.ExtraLarge)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) { imageContent() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to Color(0x80000000),
                        1f to Color(0xE6000000)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            if (chips != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = chips)
                Spacer(Modifier.height(8.dp))
            }
            Text(
                title,
                style = BoardGameDarkTypography.HeadlineMd,
                color = BoardGameDarkColors.OnSurface
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = BoardGameDarkTypography.BodyMd,
                    color = BoardGameDarkColors.OnSurfaceVariant
                )
            }
        }
        if (trailingButton != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                content = trailingButton
            )
        }
    }
}

/**
 * Dark stat card — label + large number.
 * Matches "142 Total Sessions", "64% Win Ratio" on dark Home screen.
 */
@Composable
fun BgDarkStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = BoardGameDarkColors.Primary,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = BoardGameShapes.Large,
        elevation = CardDefaults.cardElevation(BoardGameDarkElevation.Low),
        colors = CardDefaults.cardColors(containerColor = BoardGameDarkColors.SurfaceContainerHigh),
        border = BorderStroke(0.5.dp, BoardGameDarkColors.OutlineVariant.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                leadingIcon(); Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(
                    label.uppercase(),
                    style = BoardGameDarkTypography.LabelLg,
                    color = BoardGameDarkColors.OnSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(value, style = BoardGameDarkTypography.HeadlineLgMobile, color = valueColor)
            }
        }
    }
}

/**
 * Dark settings row — icon tile + title + subtitle + chevron.
 * Matches rows inside Account Settings on dark Profile screen.
 */
@Composable
fun BgDarkSettingsRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconBg: Color = BoardGameDarkColors.SurfaceContainerHigh,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(BoardGameShapes.Small)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = BoardGameDarkColors.OnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = BoardGameDarkTypography.BodyLg,
                color = BoardGameDarkColors.OnSurface
            )
            if (subtitle != null) Text(
                subtitle,
                style = BoardGameDarkTypography.BodyMd,
                color = BoardGameDarkColors.OnSurfaceVariant
            )
        }
        trailingContent?.invoke()
            ?: Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                tint = BoardGameDarkColors.Outline,
                modifier = Modifier.size(20.dp)
            )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Cards — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CardsLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BgListCard(onClick = {}) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(BoardGameShapes.Medium)
                            .background(BoardGameColors.SurfaceContainerHigh)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Everdell",
                            style = BoardGameTypography.TitleLg,
                            color = BoardGameColors.OnSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            BgChip("STRATEGY", BgChipStyle.CATEGORY)
                            BgChip("2018", BgChipStyle.YEAR)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BgStatCard("Total Games", "142", modifier = Modifier.weight(1f))
                BgStatCard("Win Ratio", "64%", modifier = Modifier.weight(1f))
            }
            BgSettingsRow(
                Icons.Default.Settings, "Account Settings",
                subtitle = "Email, Password, Personal Details", onClick = {})
        }
    }
}

@Preview(name = "Cards — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CardsDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BgDarkListCard(onClick = {}) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(BoardGameShapes.Medium)
                            .background(BoardGameDarkColors.SurfaceContainerHigh)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Dune: Imperium",
                            style = BoardGameDarkTypography.TitleLg,
                            color = BoardGameDarkColors.OnSurface
                        )
                        Text(
                            "Deck Building / Worker Placement",
                            style = BoardGameDarkTypography.BodyMd,
                            color = BoardGameDarkColors.OnSurfaceVariant
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BgDarkStatCard("Total Sessions", "142", modifier = Modifier.weight(1f))
                BgDarkStatCard(
                    "Win Ratio", "64%", modifier = Modifier.weight(1f),
                    valueColor = BoardGameDarkColors.Tertiary
                )
            }
            BgDarkSettingsRow(
                Icons.Default.Settings, "Account Information",
                subtitle = "Email, Password, Personal Details", onClick = {})
        }
    }
}
