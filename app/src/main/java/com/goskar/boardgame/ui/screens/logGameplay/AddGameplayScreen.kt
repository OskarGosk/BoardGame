package com.goskar.boardgame.ui.screens.logGameplay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppSelectChip
import com.goskar.boardgame.ui.components.AppSelectChipStyle
import com.goskar.boardgame.ui.components.AppSelectField
import com.goskar.boardgame.ui.components.AppSelectOption
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerScreen
import com.goskar.boardgame.ui.screens.logGameplay.components.AddPlayerButton
import com.goskar.boardgame.ui.screens.logGameplay.components.CoopResultTile
import com.goskar.boardgame.ui.screens.logGameplay.components.DatePlayedField
import com.goskar.boardgame.ui.screens.logGameplay.components.PlayerToggle
import com.goskar.boardgame.ui.screens.logGameplay.components.WinnerTile
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.ProfileScreen
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import java.time.LocalDate
import org.koin.androidx.compose.koinViewModel

class AddGameplayScreen(
    private val editSessionId: String? = null,
    private val preselectedGameId: String? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameplayViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) { viewModel.load(editSessionId, preselectedGameId) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddGameplayEvent.Saved -> {
                        snackbarHostState.show(resources.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is AddGameplayEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)
                }
            }
        }

        AddGameplayScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onSelectGame = viewModel::selectGame,
            onDateSelected = viewModel::updatePlayDate,
            onCancel = { navigator?.pop() },
            onAddPlayer = { navigator?.push(AddPlayerScreen()) },
            onTogglePlayer = viewModel::togglePlayer,
            onToggleVariant = viewModel::toggleVariant,
            onSelectWinner = viewModel::selectWinner,
            onSelectCoopWin = viewModel::selectCoopWin,
            onNotesChange = viewModel::updateNotes,
            onDurationChange = viewModel::updateDuration,
            onScoreChange = viewModel::updatePlayerScore,
            onLogSession = viewModel::logSession,
            onProfileClick = { navigator?.push(ProfileScreen()) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddGameplayScreenContent(
    state: AddGameplayState,
    userInitials: String = "AM",
    onSelectGame: (String) -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onTogglePlayer: (Int) -> Unit = {},
    onToggleVariant: (Int) -> Unit = {},
    onSelectWinner: (Int) -> Unit = {},
    onSelectCoopWin: (Boolean) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onDurationChange: (String) -> Unit = {},
    onScoreChange: (Int, String) -> Unit = { _, _ -> },
    onLogSession: () -> Unit = {},
    onCancel: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(2) }

    AppScaffold(
        title = if (state.isEditMode) stringResource(R.string.log_title_edit) else stringResource(R.string.log_title_new),
        subtitle = stringResource(R.string.log_subtitle),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = {
            AppAvatar(
                initials = userInitials,
                size = 36.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
            )
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
                AppSelectField(
                    label = stringResource(R.string.log_select_game),
                    value = state.selectedGame,
                    placeholder = stringResource(R.string.log_select_a_game),
                    options = state.availableGames.map { AppSelectOption(it.id, it.name) },
                    onSelect = onSelectGame,
                )
            }

            AppListCard {
                DatePlayedField(
                    value = state.datePlayed,
                    date = state.playDate,
                    onDateSelected = onDateSelected,
                )
            }

            AppListCard {
                AppTextField(
                    value = state.durationMin,
                    onValueChange = onDurationChange,
                    label = stringResource(R.string.log_duration_label),
                    placeholder = stringResource(R.string.log_duration_hint),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            AppListCard {
                AppSectionHeader(
                    title = stringResource(R.string.section_players),
                    action = stringResource(R.string.log_add_new),
                    onAction = onAddPlayer
                )
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

            if (state.variants.isNotEmpty()) {
                AppListCard {
                    AppSectionHeader(title = stringResource(R.string.section_session_variants))
                    Spacer(Modifier.height(16.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        state.variants.forEachIndexed { index, variant ->
                            AppSelectChip(
                                style = AppSelectChipStyle.FILLED,
                                text = variant.name,
                                selected = variant.selected,
                                onToggle = { onToggleVariant(index) },
                            )
                        }
                    }
                }
            }

            AppListCard {
                AppSectionHeader(
                    title = if (state.cooperate) stringResource(R.string.log_coop_result)
                    else stringResource(R.string.log_select_winner)
                )
                Spacer(Modifier.height(16.dp))
                if (state.cooperate) {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CoopResultTile(
                            label = stringResource(R.string.log_coop_players_won),
                            selected = state.coopPlayersWon == true,
                            onClick = { onSelectCoopWin(true) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                        CoopResultTile(
                            label = stringResource(R.string.log_coop_computer_won),
                            selected = state.coopPlayersWon == false,
                            onClick = { onSelectCoopWin(false) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                } else {
                    val winnerCandidates = state.players.withIndex().filter { it.value.selected }
                    if (winnerCandidates.isEmpty()) {
                        Text(
                            stringResource(R.string.log_select_players_first),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            winnerCandidates.chunked(2).forEach { rowPlayers ->
                                Row(
                                    modifier = Modifier.height(IntrinsicSize.Min),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    rowPlayers.forEach { (absoluteIndex, player) ->
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
                }
            }

            val scoredPlayers = state.players.withIndex().filter { it.value.selected }
            if (scoredPlayers.isNotEmpty()) {
                AppListCard {
                    AppSectionHeader(title = stringResource(R.string.log_scores_title))
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        scoredPlayers.forEach { (index, player) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppAvatar(initials = player.initials, size = 36.dp)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    player.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                AppTextField(
                                    value = player.score,
                                    onValueChange = { onScoreChange(index, it) },
                                    placeholder = "0",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(88.dp),
                                )
                            }
                        }
                    }
                }
            }

            AppListCard {
                AppSectionHeader(title = stringResource(R.string.log_session_notes))
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    singleLine = false,
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = stringResource(R.string.log_notes_hint),
                )
            }

            AppPrimaryButton(
                text = if (state.isEditMode) stringResource(R.string.log_button_update) else stringResource(
                    R.string.log_button_new
                ),
                onClick = onLogSession,
                leadingIcon = {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
            )
            AppSecondaryButton(text = stringResource(R.string.cancel), onClick = onCancel)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(name = "Log Gameplay — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameplayLightPreview() {
    BoardGameTheme(darkTheme = false) { AddGameplayScreenContent(state = AddGameplayState()) }
}

@Preview(name = "Log Gameplay — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddGameplayDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddGameplayScreenContent(state = AddGameplayState()) }
}
