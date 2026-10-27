package com.goskar.boardgame.ui.screens.sessionDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.*
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.SessionPlayerResult
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt


@Composable
fun PlayerResultRow(
    modifier: Modifier = Modifier,
    player: SessionPlayerResult,
) {
    val winColor = appExt().success
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AppAvatar(initials = player.initials, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                player.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (player.isWinner) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = if (player.isWinner) winColor else MaterialTheme.colorScheme.onSurface,
            )
            player.team?.let { team ->
                Text(
                    stringResource(R.string.log_team_number, team),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (player.isWinner) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = winColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.label_winner), style = MaterialTheme.typography.labelMedium, color = winColor)
                }
            }
        }
        if (player.score.isNotBlank()) {
            Text(
                stringResource(R.string.session_score_pts, player.score),
                style = MaterialTheme.typography.titleLarge,
                color = if (player.isWinner) winColor else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}


@Composable
private fun PlayerResultRowPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PlayerResultRow(player = SessionPlayerResult(name = "Alex M.", initials = "AM", score = "42", isWinner = true))
            PlayerResultRow(player = SessionPlayerResult(name = "Sarah K.", initials = "SK", score = "37"))
            PlayerResultRow(player = SessionPlayerResult(name = "Kamila", initials = "K", score = "", isWinner = true, team = 1))
        }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun PlayerResultRowLight() = BoardGameTheme(darkTheme = false) { PlayerResultRowPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PlayerResultRowDark() = BoardGameTheme(darkTheme = true) { PlayerResultRowPreviewContent() }
