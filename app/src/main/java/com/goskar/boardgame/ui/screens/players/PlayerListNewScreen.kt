package com.goskar.boardgame.ui.screens.players

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.playerList.PlayerListState
import com.goskar.boardgame.ui.playerList.PlayerListViewModel
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerNewScreen
import com.goskar.boardgame.ui.screens.addPlayer.viewmodel.EditPlayerData
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppPlayerRow
import com.goskar.boardgame.ui.theme.AppSearchBar
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSecondaryButton
import com.goskar.boardgame.ui.theme.AppStatCard
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import org.koin.androidx.compose.koinViewModel

data class DirectoryPlayer(
    val name: String,
    val role: String,
    val initials: String,
    val winRate: Double,
    val rank: String,
    val online: Boolean,
)

data class PlayerListNewState(
    val query: String = "",
    val totalPlayers: String = "0",
    val avgWinRate: String = "0%",
    val activeThisWeek: String = "—",
    val players: List<DirectoryPlayer> = emptyList(),
)

class PlayerListNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: PlayerListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) { viewModel.getAllPlayer() }

        PlayerListNewScreenContent(
            state = state.toDirectoryState(),
            onQueryChange = viewModel::updateSearchTxt,
            onPlayerClick = { player ->
                navigator?.push(AddPlayerNewScreen(EditPlayerData(name = player.name)))
            },
            onAddPlayerClick = {
                navigator?.push(AddPlayerNewScreen())
            },
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
        )
    }
}

private fun PlayerListState.toDirectoryState(): PlayerListNewState {
    val all = playerList ?: emptyList()
    val ranked = all.sortedByDescending { it.winRatio }
    val visible = if (searchTxt.isBlank()) ranked
    else ranked.filter { it.name.contains(searchTxt, ignoreCase = true) }

    val players = visible.map { p ->
        DirectoryPlayer(
            name = p.name,
            role = (p.description.ifBlank { "Player" }) + " · ${p.games} games",
            initials = playerInitials(p.name),
            winRate = p.winRatio.toDouble(),
            rank = "#${ranked.indexOf(p) + 1}",
            online = false,
        )
    }
    val avg = if (all.isNotEmpty()) all.sumOf { it.winRatio } / all.size else 0
    return PlayerListNewState(
        query = searchTxt,
        totalPlayers = all.size.toString(),
        avgWinRate = "$avg%",
        activeThisWeek = "—",
        players = players,
    )
}

private fun playerInitials(name: String): String =
    name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
        .joinToString("").ifBlank { name.take(2) }.uppercase()

@Composable
fun PlayerListNewScreenContent(
    state: PlayerListNewState,
    onMenu: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onPlayerClick: (DirectoryPlayer) -> Unit = {},
    onPlayerMenu: (DirectoryPlayer) -> Unit = {},
    onAddPlayerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(3) }

    AppScaffold(
        title = "Tabletop Tracker",
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = {
            AppAvatar(
                initials = "AM",
                size = 36.dp,
                modifier = Modifier.clip(CircleShape).clickable { onProfileClick() },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    "Players Directory",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "Manage and track your gaming circle's statistics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AppSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "Search players by name or rank…",
            )

            AppStatCard(
                label = "Total Players",
                value = state.totalPlayers,
                modifier = Modifier.fillMaxWidth(),
                icon = { StatIcon(Icons.Default.Group) },
            )
            AppStatCard(
                label = "Avg Win Rate",
                value = state.avgWinRate,
                modifier = Modifier.fillMaxWidth(),
                valueColor = appExt().success,
                icon = {
                    StatIcon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        bg = appExt().success.copy(alpha = 0.18f),
                        tint = appExt().success,
                    )
                },
            )
            AppStatCard(
                label = "Active This Week",
                value = state.activeThisWeek,
                modifier = Modifier.fillMaxWidth(),
                icon = { StatIcon(Icons.Default.MilitaryTech) },
            )

            Spacer(Modifier.height(4.dp))

            state.players.forEach { player ->
                AppPlayerRow(
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
            AppSecondaryButton(text = "ADD PLAYERS", onClick = onAddPlayerClick)
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun winRateColor(rate: Double): Color = when {
    rate >= 60.0 -> appExt().success
    rate < 45.0 -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurface
}

@Composable
private fun StatIcon(
    icon: ImageVector,
    bg: Color = MaterialTheme.colorScheme.secondaryContainer,
    tint: Color = MaterialTheme.colorScheme.primary,
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

@Preview(name = "Players Directory — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun PlayerListNewScreenLightPreview() {
    BoardGameTheme(darkTheme = false) { PlayerListNewScreenContent(state = PlayerListNewState()) }
}

@Preview(name = "Players Directory — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PlayerListNewScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) { PlayerListNewScreenContent(state = PlayerListNewState()) }
}
