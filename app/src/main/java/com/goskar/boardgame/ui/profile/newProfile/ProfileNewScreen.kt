package com.goskar.boardgame.ui.profile.newProfile

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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.theme.BgDarkAvatar
import com.goskar.boardgame.ui.theme.BgDarkBottomNavBar
import com.goskar.boardgame.ui.theme.BgDarkChip
import com.goskar.boardgame.ui.theme.BgDarkChipStyle
import com.goskar.boardgame.ui.theme.BgDarkListCard
import com.goskar.boardgame.ui.theme.BgDarkSecondaryButton
import com.goskar.boardgame.ui.theme.BgDarkSettingsRow
import com.goskar.boardgame.ui.theme.BgDarkStatCard
import com.goskar.boardgame.ui.theme.BgDarkTopBar
import com.goskar.boardgame.ui.theme.BgNavItem
import com.goskar.boardgame.ui.theme.BoardGameDarkColors
import com.goskar.boardgame.ui.theme.BoardGameDarkSpacing
import com.goskar.boardgame.ui.theme.BoardGameDarkTypography
import com.goskar.boardgame.ui.theme.BoardGameShapes
import org.koin.androidx.compose.koinViewModel

private val darkNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
    BgNavItem("Add Session", Icons.Default.Add),
    BgNavItem("Profile", Icons.Default.Person),
)

/**
 * New "Profile / Settings" screen (dark redesign). Backed by [ProfileNewViewModel]
 * (placeholder state only).
 */
class ProfileNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: ProfileNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        ProfileNewScreenContent(
            state = state,
            onSignOut = { navigator?.pop() },
        )
    }
}

@Composable
fun ProfileNewScreenContent(
    state: ProfileNewState,
    onMenu: () -> Unit = {},
    onSetting: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onForceSync: () -> Unit = {},
    onViewAchievements: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(3) } // Profile tab

    Scaffold(
        containerColor = BoardGameDarkColors.Background,
        topBar = {
            BgDarkTopBar(
                title = "Tabletop Tracker",
                showMenu = true,
                onMenuClick = onMenu,
                trailingContent = { BgDarkAvatar(initials = state.initials, size = 36.dp) },
            )
        },
        bottomBar = {
            BgDarkBottomNavBar(darkNavItems, selectedNav, { selectedNav = it })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameDarkSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileHeaderCard(state)
            StatRow(state)
            AccountSettingsCard(state, onSetting)
            SignOutButton(onSignOut)
            CloudSyncCard(state, onForceSync)
            RecentMedalsCard(state, onViewAchievements)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileHeaderCard(state: ProfileNewState) {
    BgDarkListCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            BgDarkAvatar(
                initials = state.initials,
                size = 80.dp,
                selected = true,
                onlineStatus = true,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                state.name,
                style = BoardGameDarkTypography.HeadlineMd,
                color = BoardGameDarkColors.OnSurface,
            )
            Text(
                state.subtitle,
                style = BoardGameDarkTypography.BodyMd,
                color = BoardGameDarkColors.OnSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BgDarkChip("Pro Strategist", BgDarkChipStyle.BASE_GAME)
                BgDarkChip("Daily Player", BgDarkChipStyle.STATUS_WIN)
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun StatRow(state: ProfileNewState) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BgDarkStatCard(
            label = "Games Logged",
            value = state.gamesLogged,
            modifier = Modifier.weight(1f),
            valueColor = BoardGameDarkColors.OnSurface,
            leadingIcon = {
                Icon(
                    Icons.Default.Casino,
                    contentDescription = null,
                    tint = BoardGameDarkColors.Primary,
                    modifier = Modifier.size(24.dp),
                )
            },
        )
        BgDarkStatCard(
            label = "Win Rate",
            value = state.winRate,
            modifier = Modifier.weight(1f),
            valueColor = BoardGameDarkColors.Success,
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = BoardGameDarkColors.Success,
                    modifier = Modifier.size(24.dp),
                )
            },
        )
    }
}

@Composable
private fun AccountSettingsCard(state: ProfileNewState, onSetting: (String) -> Unit) {
    BgDarkListCard {
        CardHeading("Account Settings")
        Spacer(Modifier.height(4.dp))
        BgDarkSettingsRow(
            icon = Icons.Default.Person,
            title = "Account Information",
            subtitle = "Email, Password, Personal Details",
            onClick = { onSetting("account") },
        )
        RowDivider()
        BgDarkSettingsRow(
            icon = Icons.Default.Notifications,
            title = "Game Notifications",
            subtitle = "Turn alerts, Session reminders",
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.notificationsActive) {
                        BgDarkChip("Active", BgDarkChipStyle.STATUS_WIN)
                        Spacer(Modifier.width(8.dp))
                    }
                    Chevron()
                }
            },
            onClick = { onSetting("notifications") },
        )
        RowDivider()
        BgDarkSettingsRow(
            icon = Icons.Default.Security,
            title = "Privacy & Security",
            subtitle = "Manage visibility and 2FA",
            onClick = { onSetting("privacy") },
        )
        RowDivider()
        BgDarkSettingsRow(
            icon = Icons.Default.Palette,
            title = "Appearance",
            subtitle = "Dark mode, Theme accents",
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
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = BoardGameDarkColors.Error,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Sign Out of Session",
            style = BoardGameDarkTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
            color = BoardGameDarkColors.Error,
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
            .background(BoardGameDarkColors.SurfaceContainer),
    ) {
        // Green left accent
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(BoardGameDarkColors.Success),
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Cloud Sync",
                    style = BoardGameDarkTypography.HeadlineMd,
                    color = BoardGameDarkColors.OnSurface,
                )
                Icon(
                    Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = BoardGameDarkColors.Success,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "All session data is currently synchronized across your devices.",
                style = BoardGameDarkTypography.BodyMd,
                color = BoardGameDarkColors.OnSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    state.lastSynced,
                    style = BoardGameDarkTypography.BodyMd,
                    color = BoardGameDarkColors.OnSurfaceVariant,
                )
                SmallPillButton("Force Sync", onForceSync)
            }
        }
    }
}

@Composable
private fun RecentMedalsCard(state: ProfileNewState, onViewAchievements: () -> Unit) {
    BgDarkListCard {
        CardHeading("Recent Medals")
        Spacer(Modifier.height(8.dp))
        state.medals.forEachIndexed { index, medal ->
            BgDarkSettingsRow(
                icon = if (index == 0) Icons.Default.MilitaryTech else Icons.Default.Group,
                title = medal.title,
                subtitle = medal.description,
                trailingContent = {},
            )
            if (index < state.medals.lastIndex) Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(12.dp))
        BgDarkSecondaryButton(text = "View All Achievements", onClick = onViewAchievements)
    }
}

@Composable
private fun CardHeading(text: String) {
    Text(
        text,
        style = BoardGameDarkTypography.HeadlineMd,
        color = BoardGameDarkColors.OnSurface,
    )
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(color = BoardGameDarkColors.CardBorder, thickness = 0.5.dp)
}

@Composable
private fun RowDivider() {
    HorizontalDivider(color = BoardGameDarkColors.Divider, thickness = 0.5.dp)
}

@Composable
private fun Chevron() {
    Icon(
        Icons.Default.KeyboardArrowRight,
        contentDescription = null,
        tint = BoardGameDarkColors.Outline,
        modifier = Modifier.size(20.dp),
    )
}

@Composable
private fun SmallPillButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(BoardGameShapes.Full)
            .background(BoardGameDarkColors.SurfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = BoardGameDarkTypography.LabelLg,
            color = BoardGameDarkColors.OnSurface,
        )
    }
}

@Preview(name = "Profile — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ProfileNewScreenPreview() {
    ProfileNewScreenContent(state = ProfileNewState())
}
