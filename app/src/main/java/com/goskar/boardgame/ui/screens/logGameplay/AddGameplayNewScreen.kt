package com.goskar.boardgame.ui.screens.logGameplay
import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.*

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppDropdownField
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppTextArea
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.components.AppVariantChip
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class AddGameplayNewScreen(
    private val editSessionId: String? = null,
    private val preselectedGameId: String? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameplayNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LaunchedEffect(Unit) { viewModel.load(editSessionId, preselectedGameId) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddGameplayEvent.Saved -> {
                        snackbarHostState.show(context.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is AddGameplayEvent.ShowMessage ->
                        snackbarHostState.show(context.getString(event.message), event.type)
                }
            }
        }

        AddGameplayNewScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onSelectGame = viewModel::selectGame,
            onDateSelected = viewModel::updatePlayDate,
            onCancel = { navigator?.pop() },
            onAddPlayer = { navigator?.push(AddPlayerNewScreen()) },
            onTogglePlayer = viewModel::togglePlayer,
            onToggleVariant = viewModel::toggleVariant,
            onSelectWinner = viewModel::selectWinner,
            onSelectCoopWin = viewModel::selectCoopWin,
            onNotesChange = viewModel::updateNotes,
            onDurationChange = viewModel::updateDuration,
            onScoreChange = viewModel::updatePlayerScore,
            onLogSession = viewModel::logSession,
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddGameplayNewScreenContent(
    state: AddGameplayNewState,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    if (state.isEditMode) stringResource(R.string.log_title_edit) else stringResource(R.string.log_title_new),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.log_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AppListCard {
                GamePickerField(
                    label = stringResource(R.string.log_select_game),
                    value = state.selectedGame,
                    options = state.availableGames,
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
                    placeholder = "e.g. 45",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            AppListCard {
                AppSectionHeader(title = stringResource(R.string.section_players), action = stringResource(R.string.log_add_new), onAction = onAddPlayer)
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
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                        CoopResultTile(
                            label = stringResource(R.string.log_coop_computer_won),
                            selected = state.coopPlayersWon == false,
                            onClick = { onSelectCoopWin(false) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
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
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                AppTextArea(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = stringResource(R.string.log_notes_hint),
                )
            }

            AppPrimaryButton(
                text = if (state.isEditMode) stringResource(R.string.log_button_update) else stringResource(R.string.log_button_new),
                onClick = onLogSession,
                leadingIcon = {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                },
            )
            AppSecondaryButton(text = stringResource(R.string.cancel), onClick = onCancel)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePlayedField(
    value: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    AppDropdownField(label = stringResource(R.string.log_date_played), value = value, onClick = { showDialog = true })

    if (showDialog) {
        val initialMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            onDateSelected(selected)
                        }
                        showDialog = false
                    },
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun GamePickerField(
    label: String,
    value: String,
    options: List<GameOption>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AppDropdownField(
            label = label,
            value = value.ifBlank { stringResource(R.string.log_select_a_game) },
            onClick = { if (options.isNotEmpty()) expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelect(option.id)
                        expanded = false
                    },
                )
            }
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
private fun CoopResultTile(
    label: String,
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
            .padding(vertical = 20.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
