package com.goskar.boardgame.ui.screens.sessionDetails

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppGameCover
import com.goskar.boardgame.ui.components.AppGhostButton
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppStatCell
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.sessionDetails.components.PlayerResultRow
import com.goskar.boardgame.ui.screens.sessionDetails.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
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
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit), tint = MaterialTheme.colorScheme.primary)
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
                    AppGameCover(uri = state.coverUri, size = 72.dp)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            state.gameName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.category.isNotBlank()) {
                                AppChip(text = state.category, style = state.categoryStyle)
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
                    AppStatCell(label = stringResource(R.string.session_stat_date), value = state.dateLabel, modifier = Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    AppStatCell(label = stringResource(R.string.session_stat_duration), value = state.duration, modifier = Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    AppStatCell(label = stringResource(R.string.session_stat_players), value = state.playerCount, modifier = Modifier.weight(1f))
                }
            }

            AppSectionHeader(title = stringResource(R.string.section_players))
            AppListCard {
                state.players.forEachIndexed { index, player ->
                    PlayerResultRow(player = player)
                    if (index < state.players.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            if (state.variants.isNotEmpty()) {
                AppSectionHeader(title = stringResource(R.string.section_session_variants))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.variants.forEach { variant ->
                        AppChip(text = variant, style = AppChipStyle.EXPANSION)
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
