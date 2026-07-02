package com.goskar.boardgame.ui.home.newHome

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppBottomNavBar
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import com.goskar.boardgame.ui.theme.AppFab
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppProgressBar
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.AppStatCard
import com.goskar.boardgame.ui.theme.AppTopBar
import com.goskar.boardgame.ui.theme.BgNavItem
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

/**
 * New Home screen (redesign) — theme-aware, lives ALONGSIDE the existing
 * [com.goskar.boardgame.ui.home.HomeScreen]. Backed by [HomeNewViewModel] (placeholder data).
 */
class HomeNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: HomeNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        HomeNewScreenContent(state = state)
    }
}

/** Canonical bottom-nav destinations shared across the redesigned app. */
val appNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
    BgNavItem("Add Session", Icons.Default.Add),
    BgNavItem("Players", Icons.Default.Person),
)

@Composable
fun HomeNewScreenContent(
    state: HomeNewState,
    onSettingsClick: () -> Unit = {},
    onFabClick: () -> Unit = {},
    onViewAllSessions: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Tabletop Tracker",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        AppAvatar(initials = state.userName, size = 36.dp)
                    }
                },
            )
        },
        bottomBar = {
            AppBottomNavBar(appNavItems, selectedTab, { selectedTab = it })
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

            // Greeting
            Column {
                Text(
                    state.greeting,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    state.userName,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Total games — full width
            AppStatCard(
                label = "Total Games",
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

            // Most played + Win ratio (with progress) — equal height via IntrinsicSize
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppStatCard(
                    label = "Most Played",
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

            // Quick Actions
            AppSectionHeader(title = "Quick Actions")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionTile(Icons.Default.Add, "Add Gameplay", Modifier.weight(1f))
                QuickActionTile(Icons.Default.QrCodeScanner, "Scan BGG", Modifier.weight(1f))
                QuickActionTile(Icons.Default.Assessment, "Quick Report", Modifier.weight(1f))
            }

            // Recent Sessions
            AppSectionHeader(
                title = "Recent Sessions",
                action = "VIEW ALL",
                onAction = onViewAllSessions,
            )
            state.recentSessions.forEach { session ->
                RecentSessionCard(session)
            }

            Spacer(Modifier.height(80.dp)) // room for FAB
        }
    }
}

@Composable
private fun WinRatioCard(
    value: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    AppListCard(modifier = modifier) {
        Text(
            "WIN RATIO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(10.dp))
        AppProgressBar(progress = progress)
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RecentSessionCard(session: RecentSession) {
    AppListCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
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

@Preview(name = "Home New — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun HomeNewScreenLightPreview() {
    BoardGameTheme(darkTheme = false) {
        HomeNewScreenContent(state = HomeNewState())
    }
}

@Preview(name = "Home New — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HomeNewScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        HomeNewScreenContent(state = HomeNewState())
    }
}
