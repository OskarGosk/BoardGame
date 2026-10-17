package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppCardHeading
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.ProfileNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun AccountSettingsCard(
    modifier: Modifier = Modifier,
    state: ProfileNewState,
    onSetting: (String) -> Unit,
) {
    AppListCard(modifier = modifier) {
        AppCardHeading(text = stringResource(R.string.profile_account_settings))
        Spacer(Modifier.height(4.dp))
        AppSettingsRow(
            icon = Icons.Default.Person,
            title = stringResource(R.string.profile_account_info),
            subtitle = stringResource(R.string.profile_account_info_sub),
            onClick = { onSetting("account") },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        AppSettingsRow(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.profile_notifications),
            subtitle = stringResource(R.string.profile_notifications_sub),
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.notificationsActive) {
                        AppChip(text = stringResource(R.string.profile_active), style = AppChipStyle.STATUS_WIN)
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            onClick = { onSetting("notifications") },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        AppSettingsRow(
            icon = Icons.Default.Security,
            title = stringResource(R.string.profile_privacy),
            subtitle = stringResource(R.string.profile_privacy_sub),
            onClick = { onSetting("privacy") },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        AppSettingsRow(
            icon = Icons.Default.Palette,
            title = stringResource(R.string.profile_appearance),
            subtitle = stringResource(R.string.profile_appearance_sub),
            onClick = { onSetting("appearance") },
        )
    }
}


@Composable
private fun AccountSettingsCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        AccountSettingsCard(state = ProfileNewState(), onSetting = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AccountSettingsCardLight() = BoardGameTheme(darkTheme = false) { AccountSettingsCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AccountSettingsCardDark() = BoardGameTheme(darkTheme = true) { AccountSettingsCardPreviewContent() }
