package com.goskar.boardgame.ui.gamesList.newAddGame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppBottomNavBar
import com.goskar.boardgame.ui.theme.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSearchBar
import com.goskar.boardgame.ui.theme.AppSecondaryButton
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.AppSegmentedControl
import com.goskar.boardgame.ui.theme.AppTextField
import com.goskar.boardgame.ui.theme.AppToggleRow
import com.goskar.boardgame.ui.theme.AppTopBar
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

/**
 * New "Add Game" screen (theme-aware) with BGG Search + Manual Entry tabs.
 * Backed by [AddGameNewViewModel] (form state only).
 */
class AddGameNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        AddGameNewScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onQueryChange = viewModel::updateQuery,
            onSelectTab = viewModel::selectTab,
            onNameChange = viewModel::updateName,
            onMinChange = viewModel::updateMinPlayers,
            onMaxChange = viewModel::updateMaxPlayers,
            onToggleCooperate = viewModel::toggleCooperate,
            onToggleExpansion = viewModel::toggleExpansion,
            onSelectBaseGame = viewModel::selectBaseGame,
        )
    }
}

@Composable
fun AddGameNewScreenContent(
    state: AddGameNewState,
    onBack: () -> Unit = {},
    onHelp: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    onAddGame: (PopularMatch) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onMinChange: (String) -> Unit = {},
    onMaxChange: (String) -> Unit = {},
    onToggleCooperate: (Boolean) -> Unit = {},
    onToggleExpansion: (Boolean) -> Unit = {},
    onSelectBaseGame: (String) -> Unit = {},
    onSaveManual: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) } // Collection tab

    AppScaffold(
        title = "Add Game",
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        onBack = onBack,
        trailing = {
            IconButton(onClick = onHelp) {
                Icon(Icons.Default.HelpOutline, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
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

            AppSegmentedControl(
                options = listOf("BGG Search", "Manual Entry"),
                selected = state.selectedTab,
                onSelect = onSelectTab,
            )

            if (state.selectedTab == 0) {
                // --- BGG Search tab ---
                AppSearchBar(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = "Search BGG database…",
                )
                AppSectionHeader(title = "Popular Matches")
                state.popularMatches.forEach { match ->
                    PopularMatchCard(match = match, onAdd = { onAddGame(match) })
                }
            } else {
                // --- Manual Entry tab ---
                ManualEntryForm(
                    state = state,
                    onNameChange = onNameChange,
                    onMinChange = onMinChange,
                    onMaxChange = onMaxChange,
                    onToggleCooperate = onToggleCooperate,
                    onToggleExpansion = onToggleExpansion,
                    onSelectBaseGame = onSelectBaseGame,
                    onSave = onSaveManual,
                )
            }
        }
    }
}

@Composable
private fun ManualEntryForm(
    state: AddGameNewState,
    onNameChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    onToggleCooperate: (Boolean) -> Unit,
    onToggleExpansion: (Boolean) -> Unit,
    onSelectBaseGame: (String) -> Unit,
    onSave: () -> Unit,
) {
    CoverPhotoPicker(hasCover = state.hasCover)

    AppTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = "Game Name",
        placeholder = "e.g. Terraforming Mars",
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTextField(
            value = state.minPlayers,
            onValueChange = onMinChange,
            label = "Min Players",
            placeholder = "1",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
        AppTextField(
            value = state.maxPlayers,
            onValueChange = onMaxChange,
            label = "Max Players",
            placeholder = "4",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
    }

    AppToggleRow(
        icon = Icons.Default.Groups,
        title = "Cooperate Game?",
        description = "Players win or lose together",
        checked = state.cooperate,
        onToggle = onToggleCooperate,
    )
    AppToggleRow(
        icon = Icons.Default.Extension,
        title = "Is Expansion?",
        description = "Requires a base game to play",
        checked = state.expansion,
        onToggle = onToggleExpansion,
    )

    if (state.expansion) {
        ManualSelectField(
            label = "Base Game",
            value = state.baseGame,
            placeholder = "Select base game",
            options = state.baseGameOptions,
            onSelect = onSelectBaseGame,
        )
    }

    Spacer(Modifier.height(4.dp))
    AppPrimaryButton(
        text = "Save Game",
        onClick = onSave,
        enabled = state.name.isNotBlank(),
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CoverPhotoPicker(hasCover: Boolean) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .drawBehind {
                    drawRoundRect(
                        color = outline,
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
                        ),
                    )
                }
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    if (hasCover) "Cover added" else "Add a cover photo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppSecondaryButton(text = "Camera", onClick = {}, modifier = Modifier.weight(1f))
            AppSecondaryButton(text = "Gallery", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ManualSelectField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, BoardGameShapes.Medium)
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularMatchCard(match: PopularMatch, onAdd: () -> Unit) {
    AppListCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(match.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppChip(match.category, AppChipStyle.CATEGORY)
                    AppChip(match.year, AppChipStyle.YEAR)
                }
            }
            IconButton(onClick = onAdd) {
                Icon(
                    Icons.Default.AddCircleOutline,
                    contentDescription = "Add ${match.name}",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Preview(name = "Add Game — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameNewLightPreview() {
    BoardGameTheme(darkTheme = false) { AddGameNewScreenContent(state = AddGameNewState()) }
}

@Preview(name = "Add Game — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddGameNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddGameNewScreenContent(state = AddGameNewState()) }
}
