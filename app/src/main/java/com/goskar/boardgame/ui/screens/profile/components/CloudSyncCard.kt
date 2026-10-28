package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewState
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import com.goskar.boardgame.utils.toLocalizedDateTime


@Composable
fun CloudSyncCard(
    modifier: Modifier = Modifier,
    state: ProfileNewState,
    onForceSync: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(BoardGameShapes.Large)
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(appExt().success),
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(if (state.isGuest) R.string.profile_backup_title else R.string.profile_cloud_sync),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = appExt().success)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(if (state.isGuest) R.string.profile_backup_desc else R.string.profile_cloud_sync_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    syncStatusText(state),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                SmallPillButton(
                    text = when {
                        state.syncing -> stringResource(R.string.profile_syncing)
                        state.isGuest -> stringResource(R.string.profile_backup_action)
                        else -> stringResource(R.string.profile_force_sync)
                    },
                    onClick = { if (!state.syncing) onForceSync() },
                )
            }
        }
    }
}

@Composable
private fun syncStatusText(state: ProfileNewState): String = when {
    state.hasUnsyncedChanges -> stringResource(R.string.profile_unsynced_changes)
    state.lastSyncedAt != null -> stringResource(R.string.profile_last_synced, state.lastSyncedAt.toLocalizedDateTime())
    else -> stringResource(R.string.profile_not_synced)
}


@Composable
private fun CloudSyncCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CloudSyncCard(state = ProfileNewState(lastSyncedAt = 1_760_000_000_000L), onForceSync = {})
            CloudSyncCard(state = ProfileNewState(syncing = true), onForceSync = {})
        }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CloudSyncCardLight() = BoardGameTheme(darkTheme = false) { CloudSyncCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CloudSyncCardDark() = BoardGameTheme(darkTheme = true) { CloudSyncCardPreviewContent() }
