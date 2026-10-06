package com.goskar.boardgame.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.useCase.RecentSession
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppGameCover
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun RecentSessionCard(session: RecentSession) {
    AppListCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppGameCover(uri = session.uri, size = 56.dp)

            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        session.gameName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.size(8.dp))
                    AppChip(session.date, AppChipStyle.YEAR)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        session.playersInitials.forEachIndexed { index, initials ->
                            AppAvatar(
                                initials = initials,
                                size = 28.dp,
                                modifier = Modifier.offset(x = (index * -8).dp),
                            )
                        }
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(
                        session.winner,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun RecentSessionCardLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) {
            RecentSessionCard(
                session = RecentSession(
                    gameName = "Everdell",
                    date = "2023",
                    playersInitials = listOf("JD", "AS", "BK"),
                    winner = "Winner: JD",
                    uri = ""
                )
            )
        }
    }
}

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun RecentSessionCardDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Box(Modifier.padding(16.dp)) {
            RecentSessionCard(
                session = RecentSession(
                    gameName = "Terraforming Mars",
                    date = "2024",
                    playersInitials = listOf("MK", "PL"),
                    winner = "Winner: MK",
                    uri = ""
                )
            )
        }
    }
}
