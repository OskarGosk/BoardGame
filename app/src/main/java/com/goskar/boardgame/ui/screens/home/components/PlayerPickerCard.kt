package com.goskar.boardgame.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.screens.profile.viewmodel.PlayerPick
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun PlayerPickerCard(availablePlayers: List<PlayerPick>, onSelectPlayer: (String) -> Unit) {
    AppListCard {
        CardHeading(stringResource(R.string.picker_heading))
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.picker_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        availablePlayers.forEachIndexed { index, player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPlayer(player.id) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppAvatar(initials = player.initials, size = 40.dp)
                Spacer(Modifier.width(12.dp))
                Text(
                    player.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Chevron()
            }
            if (index < availablePlayers.lastIndex) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
        if (availablePlayers.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.picker_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CardHeading(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
}

@Composable
private fun Chevron() {
    Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(20.dp)
    )
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun PlayerPickerCardLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) {
            PlayerPickerCard(
                availablePlayers = listOf(
                    PlayerPick("1", "John Doe", "JD"),
                    PlayerPick("2", "Jane Smith", "JS")
                ),
                onSelectPlayer = {}
            )
        }
    }
}

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PlayerPickerCardDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Box(Modifier.padding(16.dp)) {
            PlayerPickerCard(
                availablePlayers = listOf(
                    PlayerPick("1", "John Doe", "JD"),
                    PlayerPick("2", "Jane Smith", "JS")
                ),
                onSelectPlayer = {}
            )
        }
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun PlayerPickerCardEmptyPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) {
            PlayerPickerCard(
                availablePlayers = emptyList(),
                onSelectPlayer = {}
            )
        }
    }
}
