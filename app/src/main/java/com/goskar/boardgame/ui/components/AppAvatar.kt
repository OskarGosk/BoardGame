package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt

@Composable
fun AppAvatar(
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
                    color = cs.onSecondaryContainer,
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
                    .border(2.dp, cs.surfaceContainerLowest, CircleShape),
            )
        }
    }
}

@Composable
private fun AppAvatarPreviewContent() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppAvatar(initials = "AM", selected = true, onlineStatus = true)
        AppAvatar(initials = "SK")
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppAvatarLight() = BoardGameTheme(darkTheme = false) { AppAvatarPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppAvatarDark() = BoardGameTheme(darkTheme = true) { AppAvatarPreviewContent() }
