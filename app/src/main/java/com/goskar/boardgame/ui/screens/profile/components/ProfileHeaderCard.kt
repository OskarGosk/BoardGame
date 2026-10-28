package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.user.UserName
import com.goskar.boardgame.ui.components.user.text
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme



@Composable
fun ProfileHeaderCard(
    modifier: Modifier = Modifier,
    state: ProfileNewState,
) {
    AppListCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            AppAvatar(initials = state.initials, size = 80.dp, selected = true)
            Spacer(Modifier.height(12.dp))
            Text(
                state.name.text(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (state.isGuestSession) stringResource(R.string.profile_guest_session) else state.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}


@Composable
private fun ProfileHeaderCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        ProfileHeaderCard(state = ProfileNewState(name = UserName.Named("Alex M."), subtitle = "Board game enthusiast", initials = "AM"))
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ProfileHeaderCardLight() = BoardGameTheme(darkTheme = false) { ProfileHeaderCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ProfileHeaderCardDark() = BoardGameTheme(darkTheme = true) { ProfileHeaderCardPreviewContent() }
