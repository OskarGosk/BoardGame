package com.goskar.boardgame.ui.gamesHistory.newAddGameplay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppBottomNavBar
import com.goskar.boardgame.ui.theme.AppDropdownField
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSecondaryButton
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.AppTextArea
import com.goskar.boardgame.ui.theme.AppTopBar
import com.goskar.boardgame.ui.theme.AppVariantChip
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

/**
 * New "Log Gameplay Session" screen (theme-aware). Backed by [AddGameplayNewViewModel]
 * (form state only).
 */
class AddGameplayNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameplayNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        AddGameplayNewScreenContent(
            state = state,
            onCancel = { navigator?.pop() },
            onTogglePlayer = viewModel::togglePlayer,
            onToggleVariant = viewModel::toggleVariant,
            onSelectWinner = viewModel::selectWinner,
            onNotesChange = viewModel::updateNotes,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddGameplayNewScreenContent(
    state: AddGameplayNewState,
    onSelectGame: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onTogglePlayer: (Int) -> Unit = {},
    onToggleVariant: (Int) -> Unit = {},
    onSelectWinner: (Int) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onLogSession: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(2) } // Add Session tab

    AppScaffold(
        title = "Tabletop Tracker",
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = { AppAvatar(initials = "AM", size = 36.dp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Column {
                Text(
                    "Log Gameplay Session",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Capture the details of your latest tabletop conquest.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Select game
            AppListCard {
                AppDropdownField(label = "Select Game", value = state.selectedGame, onClick = onSelectGame)
            }

            // Date played
            AppListCard {
                AppDropdownField(label = "Date Played", value = state.datePlayed, onClick = {})
            }

            // Players
            AppListCard {
                AppSectionHeader(title = "Players", action = "+ Add New", onAction = onAddPlayer)
                Spacer(Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.players.forEachIndexed { index, player ->
                        PlayerToggle(
                            name = player.name,
                            initials = player.initials,
                            selected = player.selected,
                            onClick = { onTogglePlayer(index) },
                        )
                    }
                    AddPlayerButton(onClick = onAddPlayer)
                }
            }

            // Session variants
            AppListCard {
                AppSectionHeader(title = "Session Variants")
                Spacer(Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.variants.forEachIndexed { index, variant ->
                        AppVariantChip(
                            text = variant.name,
                            selected = variant.selected,
                            onToggle = { onToggleVariant(index) },
                        )
                    }
                }
            }

            // Select winner
            AppListCard {
                AppSectionHeader(title = "Select Winner")
                Spacer(Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.players.chunked(2).forEachIndexed { rowIndex, rowPlayers ->
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowPlayers.forEachIndexed { colIndex, player ->
                                val absoluteIndex = rowIndex * 2 + colIndex
                                WinnerTile(
                                    name = player.name,
                                    initials = player.initials,
                                    selected = state.winnerIndex == absoluteIndex,
                                    onClick = { onSelectWinner(absoluteIndex) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                )
                            }
                            if (rowPlayers.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // Session notes
            AppListCard {
                AppSectionHeader(title = "Session Notes")
                Spacer(Modifier.height(12.dp))
                AppTextArea(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = "Briefly describe the highlights or key turning points…",
                )
            }

            // Actions
            AppPrimaryButton(
                text = "Log Session",
                onClick = onLogSession,
                leadingIcon = {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                },
            )
            AppSecondaryButton(text = "Cancel", onClick = onCancel)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PlayerToggle(
    name: String,
    initials: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer)
            .border(
                1.dp,
                if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                BoardGameShapes.Full,
            )
            .clickable { onClick() }
            .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppAvatar(initials = initials, size = 32.dp)
        Spacer(Modifier.width(8.dp))
        Text(
            name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AddPlayerButton(onClick: () -> Unit) {
    val outline = MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(44.dp)
            .drawBehind {
                drawCircle(
                    color = outline,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                    ),
                )
            }
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = "Add player",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun WinnerTile(
    name: String,
    initials: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Large)
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer)
            .border(
                if (selected) 1.5.dp else 1.dp,
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                BoardGameShapes.Large,
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppAvatar(initials = initials, size = 56.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
        if (selected) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Winner",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp),
            )
        }
    }
}

@Preview(name = "Log Gameplay — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameplayNewLightPreview() {
    BoardGameTheme(darkTheme = false) { AddGameplayNewScreenContent(state = AddGameplayNewState()) }
}

@Preview(name = "Log Gameplay — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddGameplayNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddGameplayNewScreenContent(state = AddGameplayNewState()) }
}
