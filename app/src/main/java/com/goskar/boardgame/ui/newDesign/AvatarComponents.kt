package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// AVATAR — Light
// =============================================================================

/**
 * Circular player avatar — selection ring, optional online dot.
 * Matches avatars on Add Player, Log Session, History screens.
 */
@Composable
fun BgAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    initials: String? = null,
    selected: Boolean = false,
    onlineStatus: Boolean? = null,
    imageContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    Box(modifier = modifier.size(size + if (selected) 8.dp else 0.dp)) {
        Box(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(BoardGameColors.SecondaryContainer)
                .then(
                    if (selected) Modifier.border(
                        3.dp,
                        BoardGameColors.Primary,
                        CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (imageContent != null) imageContent()
            else if (initials != null) {
                Text(
                    initials.take(2).uppercase(), style = BoardGameTypography.LabelCaps,
                    color = BoardGameColors.OnSecondaryContainer
                )
            }
        }
        if (onlineStatus != null) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(if (onlineStatus) BoardGameColors.Success else BoardGameColors.Error)
                    .border(2.dp, BoardGameColors.SurfaceContainerLowest, CircleShape)
            )
        }
    }
}

/**
 * Toggle setting row — icon circle + label + description + switch.
 * Matches "Game Invites" on Add Player (light).
 */
@Composable
fun BgToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = BoardGameShapes.Large,
        elevation = CardDefaults.cardElevation(BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = BoardGameColors.SurfaceContainerLowest),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BoardGameColors.PrimaryTint10),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BoardGameColors.Primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = BoardGameTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
                    color = BoardGameColors.OnSurface
                )
                if (description != null)
                    Text(
                        description,
                        style = BoardGameTypography.BodySm,
                        color = BoardGameColors.OnSurfaceVariant
                    )
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BoardGameColors.OnPrimary,
                    checkedTrackColor = BoardGameColors.Primary,
                    uncheckedThumbColor = BoardGameColors.Outline,
                    uncheckedTrackColor = BoardGameColors.SurfaceContainerHigh,
                    uncheckedBorderColor = BoardGameColors.OutlineVariant,
                )
            )
        }
    }
}

// =============================================================================
// AVATAR — Dark
// =============================================================================

/**
 * Dark circular avatar — orange selection ring.
 * Matches avatars on dark Players, Log Session, History screens.
 */
@Composable
fun BgDarkAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    initials: String? = null,
    selected: Boolean = false,
    onlineStatus: Boolean? = null,
    imageContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(modifier = modifier.size(size + if (selected) 6.dp else 0.dp)) {
        Box(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(BoardGameDarkColors.SecondaryContainer)
                .then(
                    if (selected) Modifier.border(
                        2.5.dp,
                        BoardGameDarkColors.Primary,
                        CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (imageContent != null) imageContent()
            else if (initials != null) {
                Text(
                    initials.take(2).uppercase(), style = BoardGameDarkTypography.LabelLg,
                    color = BoardGameDarkColors.OnSecondaryContainer
                )
            }
        }
        if (onlineStatus != null) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(if (onlineStatus) BoardGameDarkColors.Success else BoardGameDarkColors.Error)
                    .border(2.dp, BoardGameDarkColors.SurfaceContainer, CircleShape)
            )
        }
    }
}

/**
 * Dark toggle row — same API as BgToggleRow, dark palette.
 */
@Composable
fun BgDarkToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    BgDarkListCard(modifier = modifier, shape = BoardGameShapes.Large) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BoardGameDarkColors.PrimaryTint10),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = BoardGameDarkColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = BoardGameDarkTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
                    color = BoardGameDarkColors.OnSurface
                )
                if (description != null)
                    Text(
                        description,
                        style = BoardGameDarkTypography.BodyMd,
                        color = BoardGameDarkColors.OnSurfaceVariant
                    )
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BoardGameDarkColors.OnPrimary,
                    checkedTrackColor = BoardGameDarkColors.OrangeFill,
                    uncheckedThumbColor = BoardGameDarkColors.Outline,
                    uncheckedTrackColor = BoardGameDarkColors.SurfaceContainerHigh,
                    uncheckedBorderColor = BoardGameDarkColors.OutlineVariant,
                )
            )
        }
    }
}

/**
 * Dark player list row — avatar + name + role + win rate + rank.
 * Matches rows on dark Players Directory screen.
 */
@Composable
fun BgDarkPlayerRow(
    initials: String,
    name: String,
    role: String,
    winRate: String,
    rank: String,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false,
    imageContent: (@Composable BoxScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    BgDarkListCard(modifier = modifier, shape = BoardGameShapes.Large, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BgDarkAvatar(
                size = 48.dp,
                initials = initials,
                onlineStatus = isOnline,
                imageContent = imageContent
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = BoardGameDarkTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
                    color = BoardGameDarkColors.OnSurface
                )
                Text(
                    role,
                    style = BoardGameDarkTypography.BodyMd,
                    color = BoardGameDarkColors.OnSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = BoardGameDarkColors.Divider, thickness = 0.5.dp)
        Spacer(Modifier.height(8.dp))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "WIN RATE",
                    style = BoardGameDarkTypography.LabelMd,
                    color = BoardGameDarkColors.OnSurfaceVariant
                )
                Text(
                    winRate,
                    style = BoardGameDarkTypography.TitleLg,
                    color = BoardGameDarkColors.Tertiary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "RANK",
                    style = BoardGameDarkTypography.LabelMd,
                    color = BoardGameDarkColors.OnSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications, null, tint = BoardGameDarkColors.Secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        rank,
                        style = BoardGameDarkTypography.TitleLg,
                        color = BoardGameDarkColors.OnSurface
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Avatar & Toggle — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AvatarLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BgAvatar(initials = "AM", size = 48.dp, selected = true, onlineStatus = true)
                BgAvatar(initials = "SK", size = 48.dp)
                BgAvatar(initials = "JD", size = 48.dp, onlineStatus = false)
                BgAvatar(initials = "GR", size = 56.dp, selected = false)
            }
            var checked by remember { mutableStateOf(true) }
            BgToggleRow(
                Icons.Default.Notifications, "Game Invites", checked, { checked = it },
                description = "Allow being added to games"
            )
        }
    }
}

@Preview(name = "Avatar & Toggle — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AvatarDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BgDarkAvatar(initials = "AM", size = 48.dp, selected = true, onlineStatus = true)
                BgDarkAvatar(initials = "SK", size = 48.dp)
                BgDarkAvatar(initials = "JD", size = 48.dp, onlineStatus = false)
            }
            var checked by remember { mutableStateOf(true) }
            BgDarkToggleRow(
                Icons.Default.Notifications, "Game Invites", checked, { checked = it },
                description = "Allow being added to games"
            )
            BgDarkPlayerRow(
                initials = "AM", name = "Alex \"MeepleKing\" Chen",
                role = "Grand Strategist · Joined Mar 2023",
                winRate = "74.2%", rank = "#12", isOnline = true, onClick = {}
            )
        }
    }
}
