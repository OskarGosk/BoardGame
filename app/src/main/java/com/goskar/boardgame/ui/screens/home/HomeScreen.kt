package com.goskar.boardgame.ui.screens.home
import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.history.HistoryNewScreen
import com.goskar.boardgame.ui.screens.addGame.AddGameNewScreen
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppFab
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppStatCard
import com.goskar.boardgame.ui.screens.home.components.PlayerPickerCard
import com.goskar.boardgame.ui.screens.home.components.QuickActionTile
import com.goskar.boardgame.ui.screens.home.components.RecentSessionCard
import com.goskar.boardgame.ui.screens.home.components.WinRatioCard
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class HomeScreen(private val firstLogin: Boolean = false) : Screen {

    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        LaunchedEffect(firstLogin) { viewModel.load(firstLogin) }
        HomeScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onSettingsClick = { navigator?.push(ProfileNewScreen()) },
            onFabClick = { navigator?.push(AddGameplayNewScreen()) },
            onViewAllSessions = { navigator?.push(HistoryNewScreen()) },
            onAddGameplay = { navigator?.push(AddGameplayNewScreen()) },
            onScanBgg = { navigator?.push(AddGameNewScreen()) },
            onSelectPlayer = viewModel::selectPlayer,
            )
    }
}


@Composable
fun HomeScreenContent(
    state: HomeState,
    userInitials: String = "AM",
    onSettingsClick: () -> Unit = {},
    onFabClick: () -> Unit = {},
    onViewAllSessions: () -> Unit = {},
    onAddGameplay: () -> Unit = {},
    onScanBgg: () -> Unit = {},
    onQuickReport: () -> Unit = {},
    onSelectPlayer: (String) -> Unit = {},
    ) {
    var selectedTab by remember { mutableStateOf(0) }

    AppScaffold(
        navItems = appNavItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        trailing = {
            AppAvatar(
                initials = userInitials,
                size = 36.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSettingsClick() },
            )
        },
        floatingActionButton = {
            AppFab(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Column {
                Text(
                    stringResource(state.greeting),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    state.userName,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            if (state.needsPlayerSelection) {
                PlayerPickerCard(state.availablePlayers, onSelectPlayer)
            } else {

                AppStatCard(
                    label = stringResource(R.string.home_total_games),
                    value = state.totalGames,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            Icons.Default.Casino,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                    },
                )

                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppStatCard(
                        label = stringResource(R.string.home_most_played),
                        value = state.mostPlayed,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    WinRatioCard(
                        value = state.winRatio,
                        progress = state.winRatioProgress,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }

            AppSectionHeader(title = stringResource(R.string.home_quick_actions))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionTile(Icons.Default.Add, stringResource(R.string.home_add_gameplay), Modifier.weight(1f), onClick = onAddGameplay)
                QuickActionTile(Icons.Default.QrCodeScanner, stringResource(R.string.home_scan_bgg), Modifier.weight(1f), onClick = onScanBgg)
                QuickActionTile(Icons.Default.Assessment, stringResource(R.string.home_quick_report), Modifier.weight(1f), onClick = onQuickReport)
            }

            AppSectionHeader(
                title = stringResource(R.string.home_recent_sessions),
                action = stringResource(R.string.home_view_all),
                onAction = onViewAllSessions,
            )
            state.recentSessions.forEach { session ->
                RecentSessionCard(session)
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}


@Preview(name = "Home New — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun HomeScreenLightPreview() {
    BoardGameTheme(darkTheme = false) {
        HomeScreenContent(state = HomeState())
    }
}

@Preview(name = "Home New — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HomeScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        HomeScreenContent(state = HomeState())
    }
}
