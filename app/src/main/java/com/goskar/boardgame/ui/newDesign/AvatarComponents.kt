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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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


@Composable
fun BgAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    initials: String? = null,
    selected: Boolean = false,
    onlineStatus: Boolean? = null,
    imageContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    Box(modifier = modifier.size(size + if (selected) 8.dp else 0.dp)) {
        Box(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(cs.secondaryContainer)
                .then(if (selected) Modifier.border(3.dp, cs.primary, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (imageContent != null) imageContent()
            else if (initials != null) {
                Text(
                    initials.take(2).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSecondaryContainer
                )
            }
        }
        if (onlineStatus != null) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(if (onlineStatus) appExt().success else cs.error)
                    .border(2.dp, cs.surfaceContainerLowest, CircleShape)
            )
        }
    }
}

@Composable
fun BgToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = BoardGameShapes.Large,
        elevation = CardDefaults.cardElevation(BoardGameElevation.Level1),
        colors = CardDefaults.cardColors(containerColor = cs.surfaceContainerLowest),
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
                    .background(cs.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = cs.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = cs.onSurface
                )
                if (description != null)
                    Text(description, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = cs.onPrimary,
                    checkedTrackColor = cs.primary,
                    uncheckedThumbColor = cs.outline,
                    uncheckedTrackColor = cs.surfaceContainerHigh,
                    uncheckedBorderColor = cs.outlineVariant,
                )
            )
        }
    }
}


@Preview(name = "Avatar & Toggle — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AvatarLightPreview() {
    BoardGameTheme(darkTheme = false) { AvatarPreviewContent() }
}

@Preview(name = "Avatar & Toggle — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AvatarDarkPreview() {
    BoardGameTheme(darkTheme = true) { AvatarPreviewContent() }
}

@Composable
private fun AvatarPreviewContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BgAvatar(initials = "AM", size = 48.dp, selected = true, onlineStatus = true)
            BgAvatar(initials = "SK", size = 48.dp)
            BgAvatar(initials = "JD", size = 48.dp, onlineStatus = false)
        }
        var checked by remember { mutableStateOf(true) }
        BgToggleRow(
            Icons.Default.Notifications, "Game Invites", checked, { checked = it },
            description = "Allow being added to games"
        )
    }
}
