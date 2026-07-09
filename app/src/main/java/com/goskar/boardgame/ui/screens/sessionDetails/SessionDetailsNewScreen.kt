package com.goskar.boardgame.ui.screens.sessionDetails
import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import com.goskar.boardgame.ui.theme.AppGhostButton
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import org.koin.androidx.compose.koinViewModel

class SessionDetailsNewScreen(
    private val historyGameId: String? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: SessionDetailsNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current

        LaunchedEffect(navigator?.lastItem) {
            if (historyGameId != null && navigator?.lastItem == this@SessionDetailsNewScreen) {
                viewModel.load(historyGameId)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is SessionDetailsEvent.Deleted -> navigator?.pop()
                    is SessionDetailsEvent.ShowMessage -> Unit
                }
            }
        }

        SessionDetailsNewScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onEdit = {
                state.sessionId?.let { id -> navigator?.push(AddGameplayNewScreen(editSessionId = id)) }
            },
            onDelete = viewModel::delete,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionDetailsNewScreenContent(
    state: SessionDetailsNewState,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    AppScaffold(
        title = stringResource(R.string.session_details_title),
        navItems = null,
        onBack = onBack,
        trailing = {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppListCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(BoardGameShapes.Medium)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            state.gameName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.category.isNotBlank()) {
                                AppChip(state.category, state.categoryStyle)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                state.dateLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            AppListCard {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatCell(stringResource(R.string.session_stat_date), state.dateLabel, Modifier.weight(1f))
                    CellDivider()
                    StatCell(stringResource(R.string.session_stat_duration), state.duration, Modifier.weight(1f))
                    CellDivider()
                    StatCell(stringResource(R.string.session_stat_players), state.playerCount, Modifier.weight(1f))
                }
            }

            AppSectionHeader(title = stringResource(R.string.section_players))
            AppListCard {
                state.players.forEachIndexed { index, player ->
                    PlayerResultRow(player)
                    if (index < state.players.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            if (state.variants.isNotEmpty()) {
                AppSectionHeader(title = stringResource(R.string.section_session_variants))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.variants.forEach { variant ->
                        AppChip(variant, AppChipStyle.EXPANSION)
                    }
                }
            }

            AppSectionHeader(title = stringResource(R.string.section_notes))
            AppListCard {
                Text(
                    state.notes,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(4.dp))
            AppPrimaryButton(
                text = stringResource(R.string.session_edit),
                onClick = onEdit,
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                },
            )
            AppGhostButton(text = stringResource(R.string.session_delete), onClick = onDelete)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun CellDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(32.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Composable
private fun PlayerResultRow(player: SessionPlayerResult) {
    val winColor = appExt().success
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppAvatar(initials = player.initials, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                player.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (player.isWinner) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = if (player.isWinner) winColor else MaterialTheme.colorScheme.onSurface,
            )
            if (player.isWinner) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = winColor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Winner", style = MaterialTheme.typography.labelMedium, color = winColor)
                }
            }
        }
        if (player.score.isNotBlank()) {
            Text(
                player.score,
                style = MaterialTheme.typography.titleLarge,
                color = if (player.isWinner) winColor else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(name = "Session Details — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SessionDetailsNewLightPreview() {
    BoardGameTheme(darkTheme = false) { SessionDetailsNewScreenContent(state = SessionDetailsNewState()) }
}

@Preview(name = "Session Details — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SessionDetailsNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { SessionDetailsNewScreenContent(state = SessionDetailsNewState()) }
}
