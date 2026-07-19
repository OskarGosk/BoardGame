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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.SimpleAlertDialog
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerNewScreen
import com.goskar.boardgame.ui.screens.addPlayer.EditPlayerData
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppPlayerRow
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.AppStatCard
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import org.koin.androidx.compose.koinViewModel

class PlayerListScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: PlayerListViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) { viewModel.getAllPlayer() }

        PlayerListScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onQueryChange = viewModel::updateSearchTxt,
            onPlayerClick = { dirPlayer ->
                val original = state.playerList.find { it.id == dirPlayer.id }
                if (original != null) {
                    navigator?.push(
                        AddPlayerNewScreen(
                            EditPlayerData(
                                id = original.id,
                                name = original.name,
                                games = original.games,
                                winRatio = original.winRatio,
                                description = original.description,
                                selected = original.selected,
                                skillIndex = original.selectedSkill
                            )
                        )
                    )
                }
            },
            onAddPlayerCLick = { navigator?.push(AddPlayerNewScreen()) },
            onDeletePlayer = { dirPlayer ->
                val original = state.playerList?.find { it.id == dirPlayer.id }
                if (original != null) {
                    viewModel.setPlayerToDelete(original)
                }
            },
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
            )

        state.playerToDelete?.let { player ->
            SimpleAlertDialog(
                titleText = stringResource(R.string.delete, player.name),
                contentText = R.string.player_delete_info,
                onDismiss = { viewModel.setPlayerToDelete(null) },
                confirmButtonClick = {
                    viewModel.validateDeletePlayer(player)
                    viewModel.setPlayerToDelete(null)
                }
            )
        }


    }
}


@Composable
fun PlayerListScreenContent(
    state: PlayerListState,
    userInitials: String = "AM",
    onQueryChange: (String) -> Unit = {},
    onPlayerClick: (DirectoryPlayer) -> Unit = {},
    onDeletePlayer: (DirectoryPlayer) -> Unit = {},
    onAddPlayerCLick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    ) {
    var selectedNav by remember { mutableStateOf(3) }

    AppScaffold(
        title = stringResource(R.string.players_title),
        subtitle = stringResource(R.string.players_subtitle),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = {
            AppAvatar(
                initials = userInitials,
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
            AppSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.players_search_hint),
            )

            AppStatCard(
                label = stringResource(R.string.players_total),
                value = state.totalPlayers,
                modifier = Modifier.fillMaxWidth(),
                icon = { StatIcon(Icons.Default.Group) },
            )

            Spacer(Modifier.height(4.dp))

            state.players.forEach { player ->
                AppPlayerRow(
                    initials = player.initials,
                    name = player.name,
                    role = player.role,
                    winRate = "%.1f%%".format(player.winRate),
                    rank = player.rank,
                    winRateColor = winRateColor(player.winRate),
                    trailing = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clip(BoardGameShapes.Full)
                                .clickable { onDeletePlayer(player) }
                                .size(20.dp),
                        )
                    },
                    onClick = { onPlayerClick(player) },
                )
            }

            Spacer(Modifier.height(4.dp))
            AppSecondaryButton(text = stringResource(R.string.players_add_button), onClick = onAddPlayerCLick)
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
private fun PlayerListScreenLightPreview() {
    BoardGameTheme(darkTheme = false) { PlayerListScreenContent(state = PlayerListState()) }
}

@Preview(name = "Players Directory — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PlayerListScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) { PlayerListScreenContent(state = PlayerListState()) }
}
