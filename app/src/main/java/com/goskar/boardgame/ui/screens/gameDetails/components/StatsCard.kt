package com.goskar.boardgame.ui.screens.gameDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppStatCell
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt


@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    state: GameDetailsNewState,
) {
    AppListCard(modifier = modifier) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppStatCell(label = stringResource(R.string.gd_players), value = state.players.ifBlank { "—" }, modifier = Modifier.weight(1f))
            VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
            AppStatCell(label = stringResource(R.string.gd_time), value = if (state.playtime.isBlank()) "—" else stringResource(R.string.gd_minutes_value, state.playtime), modifier = Modifier.weight(1f))
            VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
            AppStatCell(label = stringResource(R.string.gd_age), value = state.age.ifBlank { "—" }, modifier = Modifier.weight(1f))
        }
        if (state.rating.isNotBlank() || state.weight.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppStatCell(
                    label = stringResource(R.string.gd_rating),
                    value = state.rating.ifBlank { "—" },
                    modifier = Modifier.weight(1f),
                    valueColor = appExt().success,
                )
                VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                AppStatCell(label = stringResource(R.string.gd_weight), value = state.weight.ifBlank { "—" }, modifier = Modifier.weight(1f))
            }
        }
    }
}


private val PreviewDetails = GameDetailsNewState(
    name = "Dune: Imperium",
    year = "2020",
    players = "1–4",
    bestPlayers = "3",
    playtime = "60–120",
    age = "14+",
    rating = "8.4",
    weight = "3.0",
    description = "Influence, intrigue, and combat in the universe of Dune.",
    categories = listOf("Strategy", "Card Game"),
    mechanics = listOf("Deck Building", "Worker Placement"),
    designers = listOf("Paul Dennen"),
    publishers = listOf("Dire Wolf"),
)

@Composable
private fun StatsCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        StatsCard(state = PreviewDetails)
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun StatsCardLight() = BoardGameTheme(darkTheme = false) { StatsCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun StatsCardDark() = BoardGameTheme(darkTheme = true) { StatsCardPreviewContent() }
