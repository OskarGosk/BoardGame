package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppStatCard
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt


@Composable
fun StatRow(
    modifier: Modifier = Modifier,
    state: ProfileNewState,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppStatCard(
            label = stringResource(R.string.profile_games_logged),
            value = state.gamesLogged,
            modifier = Modifier.weight(1f),
            valueColor = MaterialTheme.colorScheme.onSurface,
            icon = {
                Icon(
                    Icons.Default.Casino,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
        AppStatCard(
            label = stringResource(R.string.profile_win_rate),
            value = state.winRate,
            modifier = Modifier.weight(1f),
            valueColor = appExt().success,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = appExt().success,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
    }
}


@Composable
private fun StatRowPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        StatRow(state = ProfileNewState(gamesLogged = "42", winRate = "58%"))
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun StatRowLight() = BoardGameTheme(darkTheme = false) { StatRowPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun StatRowDark() = BoardGameTheme(darkTheme = true) { StatRowPreviewContent() }
