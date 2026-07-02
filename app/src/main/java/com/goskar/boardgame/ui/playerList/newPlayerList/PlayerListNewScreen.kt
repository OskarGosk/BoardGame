package com.goskar.boardgame.ui.playerList.newPlayerList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.playerList.newAddPlayer.AddPlayerNewScreen
import com.goskar.boardgame.ui.playerList.newAddPlayer.EditPlayerData
import com.goskar.boardgame.ui.theme.BgDarkAvatar
import com.goskar.boardgame.ui.theme.BgDarkBottomNavBar
import com.goskar.boardgame.ui.theme.BgDarkPlayerRow
import com.goskar.boardgame.ui.theme.BgDarkSearchBar
import com.goskar.boardgame.ui.theme.BgDarkSecondaryButton
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
    BgNavItem("Players", Icons.Default.Person),
)

/**
 * New "Players Directory" screen (dark redesign). Backed by [PlayerListNewViewModel]
 * (placeholder state only).
 */
class PlayerListNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: PlayerListNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        PlayerListNewScreenContent(
            state = state,
            onQueryChange = viewModel::updateQuery,
            onPlayerClick = { player ->
                navigator?.push(AddPlayerNewScreen(EditPlayerData(name = player.name)))
            },
        )
    }
}

@Composable
fun PlayerListNewScreenContent(
    state: PlayerListNewState,
    onMenu: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onPlayerClick: (DirectoryPlayer) -> Unit = {},
    onPlayerMenu: (DirectoryPlayer) -> Unit = {},
    onLoadMore: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(3) } // Players tab

    Scaffold(
        containerColor = BoardGameDarkColors.Background,
        topBar = {
            BgDarkTopBar(
                title = "Tabletop Tracker",
                showMenu = true,
                onMenuClick = onMenu,
                trailingContent = { BgDarkAvatar(initials = "AM", size = 36.dp) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            Column {
                Text(
                    "Players Directory",
                    style = BoardGameDarkTypography.HeadlineLgMobile,
                    color = BoardGameDarkColors.OnSurface,
                )
                Text(
                    "Manage and track your gaming circle's statistics.",
                    style = BoardGameDarkTypography.BodyMd,
                    color = BoardGameDarkColors.OnSurfaceVariant,
                )
            }

            BgDarkSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "Search players by name or rank…",
            )

            // Stat cards
            BgDarkStatCard(
                label = "Total Players",
                value = state.totalPlayers,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { StatIcon(Icons.Default.Group) },
            )
            BgDarkStatCard(
                label = "Avg Win Rate",
                value = state.avgWinRate,
                modifier = Modifier.fillMaxWidth(),
                valueColor = BoardGameDarkColors.Success,
                leadingIcon = {
                    StatIcon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        bg = BoardGameDarkColors.Success.copy(alpha = 0.18f),
                        tint = BoardGameDarkColors.Success,
                    )
                },
            )
            BgDarkStatCard(
                label = "Active This Week",
                value = state.activeThisWeek,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { StatIcon(Icons.Default.MilitaryTech) },
            )

            Spacer(Modifier.height(4.dp))

            // Player rows
            state.players.forEach { player ->
                BgDarkPlayerRow(
                    initials = player.initials,
                    name = player.name,
                    role = player.role,
                    winRate = "%.1f%%".format(player.winRate),
                    rank = player.rank,
                    isOnline = player.online,
                    winRateColor = winRateColor(player.winRate),
                    trailing = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = BoardGameDarkColors.OnSurfaceVariant,
                            modifier = Modifier
                                .clip(BoardGameShapes.Full)
                                .clickable { onPlayerMenu(player) }
                                .size(20.dp),
                        )
                    },
                    onClick = { onPlayerClick(player) },
                )
            }

            Spacer(Modifier.height(4.dp))
            BgDarkSecondaryButton(text = "⌄ LOAD MORE PLAYERS", onClick = onLoadMore)
            Spacer(Modifier.height(4.dp))
        }
    }
}

private fun winRateColor(rate: Double): Color = when {
    rate >= 60.0 -> BoardGameDarkColors.Success
    rate < 45.0 -> BoardGameDarkColors.Error
    else -> BoardGameDarkColors.OnSurface
}

@Composable
private fun StatIcon(
    icon: ImageVector,
    bg: Color = BoardGameDarkColors.SecondaryContainer,
    tint: Color = BoardGameDarkColors.Primary,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(BoardGameShapes.Medium)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Preview(name = "Players Directory — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PlayerListNewScreenPreview() {
    PlayerListNewScreenContent(state = PlayerListNewState())
}
