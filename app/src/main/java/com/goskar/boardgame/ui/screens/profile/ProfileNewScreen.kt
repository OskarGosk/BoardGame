package com.goskar.boardgame.ui.screens.profile
import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource
import com.goskar.boardgame.ui.screens.profile.viewmodel.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import androidx.compose.ui.platform.LocalContext
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.login.LoginScreen
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSecondaryButton
import com.goskar.boardgame.ui.theme.AppSettingsRow
import com.goskar.boardgame.ui.theme.AppStatCard
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import org.koin.androidx.compose.koinViewModel

class ProfileNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: ProfileNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LaunchedEffect(Unit) { viewModel.load() }

        LaunchedEffect(state.signedOut) {
            if (state.signedOut) navigator?.replaceAll(LoginScreen())
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is ProfileEvent.ShowMessage ->
                        snackbarHostState.show(context.getString(event.message), event.type)
                }
            }
        }

        ProfileNewScreenContent(
            state = state,
            onSignOut = viewModel::signOut,
            onSelectPlayer = viewModel::selectPlayer,
            onForceSync = viewModel::forceSync,
        )
    }
}

@Composable
fun ProfileNewScreenContent(
    state: ProfileNewState,
    onSetting: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onForceSync: () -> Unit = {},
    onViewAchievements: () -> Unit = {},
    onSelectPlayer: (String) -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(-1) }

    AppScaffold(
        title = stringResource(R.string.app_tabletop_tracker),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = { AppAvatar(initials = state.initials, size = 36.dp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileHeaderCard(state)
            if (state.needsPlayerSelection) {
                PlayerPickerCard(state, onSelectPlayer)
            } else {
                StatRow(state)
            }
            AccountSettingsCard(state, onSetting)
            SignOutButton(onSignOut)
            if (!state.isGuest || state.lastSynced.isNotBlank()) CloudSyncCard(state, onForceSync)
            if (state.medals.isNotEmpty()) RecentMedalsCard(state, onViewAchievements)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PlayerPickerCard(state: ProfileNewState, onSelectPlayer: (String) -> Unit) {
    AppListCard {
        CardHeading("Who are you?")
        Spacer(Modifier.height(4.dp))
        Text(
            "Pick the player that represents you to see your stats.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        state.availablePlayers.forEachIndexed { index, player ->
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
            if (index < state.availablePlayers.lastIndex) RowDivider()
        }
        if (state.availablePlayers.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "No players yet. Add a player first.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(state: ProfileNewState) {
    AppListCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            AppAvatar(initials = state.initials, size = 80.dp, selected = true, onlineStatus = true)
            Spacer(Modifier.height(12.dp))
            Text(state.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(state.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun StatRow(state: ProfileNewState) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppStatCard(
            label = stringResource(R.string.profile_games_logged),
            value = state.gamesLogged,
            modifier = Modifier.weight(1f),
            valueColor = MaterialTheme.colorScheme.onSurface,
            icon = {
                Icon(Icons.Default.Casino, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            },
        )
        AppStatCard(
            label = stringResource(R.string.profile_win_rate),
            value = state.winRate,
            modifier = Modifier.weight(1f),
            valueColor = appExt().success,
            icon = {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = appExt().success, modifier = Modifier.size(24.dp))
            },
        )
    }
}

@Composable
private fun AccountSettingsCard(state: ProfileNewState, onSetting: (String) -> Unit) {
    AppListCard {
        CardHeading(stringResource(R.string.profile_account_settings))
        Spacer(Modifier.height(4.dp))
        AppSettingsRow(
            icon = Icons.Default.Person,
            title = stringResource(R.string.profile_account_info),
            subtitle = stringResource(R.string.profile_account_info_sub),
            onClick = { onSetting("account") },
        )
        RowDivider()
        AppSettingsRow(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.profile_notifications),
            subtitle = stringResource(R.string.profile_notifications_sub),
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.notificationsActive) {
                        AppChip(stringResource(R.string.profile_active), AppChipStyle.STATUS_WIN)
                        Spacer(Modifier.width(8.dp))
                    }
                    Chevron()
                }
            },
            onClick = { onSetting("notifications") },
        )
        RowDivider()
        AppSettingsRow(
            icon = Icons.Default.Security,
            title = stringResource(R.string.profile_privacy),
            subtitle = stringResource(R.string.profile_privacy_sub),
            onClick = { onSetting("privacy") },
        )
        RowDivider()
        AppSettingsRow(
            icon = Icons.Default.Palette,
            title = stringResource(R.string.profile_appearance),
            subtitle = stringResource(R.string.profile_appearance_sub),
            onClick = { onSetting("appearance") },
        )
    }
}

@Composable
private fun SignOutButton(onSignOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(BoardGameShapes.Full)
            .clickable { onSignOut() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.profile_sign_out),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun CloudSyncCard(state: ProfileNewState, onForceSync: () -> Unit) {
    Row(
        modifier = Modifier
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
                Text(stringResource(R.string.profile_cloud_sync), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = appExt().success)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.profile_cloud_sync_desc),
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
                    state.lastSynced.ifBlank { stringResource(R.string.profile_not_synced) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SmallPillButton(
                    text = if (state.syncing) stringResource(R.string.profile_syncing)
                    else stringResource(R.string.profile_force_sync),
                    onClick = { if (!state.syncing) onForceSync() },
                )
            }
        }
    }
}

@Composable
private fun RecentMedalsCard(state: ProfileNewState, onViewAchievements: () -> Unit) {
    AppListCard {
        CardHeading(stringResource(R.string.profile_recent_medals))
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
        AppSecondaryButton(text = stringResource(R.string.profile_view_achievements), onClick = onViewAchievements)
    }
}

@Composable
private fun CardHeading(text: String) {
    Text(text, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
}

@Composable
private fun RowDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
}

@Composable
private fun Chevron() {
    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
}

@Composable
private fun SmallPillButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(BoardGameShapes.Full)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(name = "Profile — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ProfileNewLightPreview() {
    BoardGameTheme(darkTheme = false) { ProfileNewScreenContent(state = ProfileNewState()) }
}

@Preview(name = "Profile — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ProfileNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { ProfileNewScreenContent(state = ProfileNewState()) }
}
