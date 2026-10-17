package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppCardHeading
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.MedalItem
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun RecentMedalsCard(
    modifier: Modifier = Modifier,
    state: ProfileNewState,
    onViewAchievements: () -> Unit,
) {
    AppListCard(modifier = modifier) {
        AppCardHeading(text = stringResource(R.string.profile_recent_medals))
        Spacer(Modifier.height(8.dp))
        state.medals.forEachIndexed { index, medal ->
            AppSettingsRow(
                icon = if (index == 0) Icons.Default.MilitaryTech else Icons.Default.Group,
                title = medal.title,
                subtitle = medal.description,
                trailing = {},
            )
            if (index < state.medals.lastIndex) Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(12.dp))
        AppSecondaryButton(
            text = stringResource(R.string.profile_view_achievements),
            onClick = onViewAchievements
        )
    }
}


@Composable
private fun RecentMedalsCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        RecentMedalsCard(
            state = ProfileNewState(medals = listOf(MedalItem(title = "First win", description = "Win your first game"))),
            onViewAchievements = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun RecentMedalsCardLight() = BoardGameTheme(darkTheme = false) { RecentMedalsCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun RecentMedalsCardDark() = BoardGameTheme(darkTheme = true) { RecentMedalsCardPreviewContent() }
