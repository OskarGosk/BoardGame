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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.newDesign.BgSectionHeader
import com.goskar.boardgame.ui.theme.BgBottomNavBar
import com.goskar.boardgame.ui.theme.BgChip
import com.goskar.boardgame.ui.theme.BgChipStyle
import com.goskar.boardgame.ui.theme.BgListCard
import com.goskar.boardgame.ui.theme.BgNavItem
import com.goskar.boardgame.ui.theme.BgPrimaryButton
import com.goskar.boardgame.ui.theme.BgSearchBar
import com.goskar.boardgame.ui.theme.BgSecondaryButton
import com.goskar.boardgame.ui.theme.BgSegmentedControl
import com.goskar.boardgame.ui.theme.BgTextField
import com.goskar.boardgame.ui.theme.BgToggleRow
import com.goskar.boardgame.ui.theme.BgTopBar
import com.goskar.boardgame.ui.theme.BoardGameColors
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.BoardGameTypography
import org.koin.androidx.compose.koinViewModel

private val gamesNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Players", Icons.Default.Person),
    BgNavItem("Games", Icons.AutoMirrored.Filled.List),
    BgNavItem("History", Icons.Default.DateRange),
)

/**
 * New "Add Game" screen (BGG search redesign) — lives alongside the existing game-search screens.
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
    var selectedNav by remember { mutableStateOf(2) } // Games tab

    Scaffold(
        containerColor = BoardGameColors.Background,
        topBar = {
            BgTopBar(
                title = "Add Game",
                onBack = onBack,
                trailingIcon = {
                    IconButton(onClick = onHelp) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = BoardGameColors.Primary,
                        )
                    }
                },
            )
        },
        bottomBar = {
            BgBottomNavBar(gamesNavItems, selectedNav, { selectedNav = it })
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

            BgSegmentedControl(
                options = listOf("BGG Search", "Manual Entry"),
                selected = state.selectedTab,
                onSelect = onSelectTab,
            )

            if (state.selectedTab == 0) {
                // --- BGG Search tab ---
                BgSearchBar(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = "Search BGG database…",
                )
                BgSectionHeader(title = "Popular Matches")
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
    // Cover photo
    CoverPhotoPicker(hasCover = state.hasCover)

    // Game name
    BgTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = "Game Name",
        placeholder = "e.g. Terraforming Mars",
    )

    // Min / Max players
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BgTextField(
            value = state.minPlayers,
            onValueChange = onMinChange,
            label = "Min Players",
            placeholder = "1",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
        BgTextField(
            value = state.maxPlayers,
            onValueChange = onMaxChange,
            label = "Max Players",
            placeholder = "4",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
    }

    // Toggles
    BgToggleRow(
        icon = Icons.Default.Groups,
        title = "Cooperate Game?",
        description = "Players win or lose together",
        checked = state.cooperate,
        onToggle = onToggleCooperate,
    )
    BgToggleRow(
        icon = Icons.Default.Extension,
        title = "Is Expansion?",
        description = "Requires a base game to play",
        checked = state.expansion,
        onToggle = onToggleExpansion,
    )

    // Base game selector — only relevant for expansions
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
    BgPrimaryButton(
        text = "Save Game",
        onClick = onSave,
        enabled = state.name.isNotBlank(),
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CoverPhotoPicker(hasCover: Boolean) {
    val outline = BoardGameColors.OutlineVariant
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
                .background(BoardGameColors.SurfaceContainerLowest),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = BoardGameColors.Primary,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (hasCover) "Cover added" else "Add a cover photo",
                    style = BoardGameTypography.BodyLg,
                    color = BoardGameColors.OnSurfaceVariant,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BgSecondaryButton(
                text = "Camera",
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            BgSecondaryButton(
                text = "Gallery",
                onClick = {},
                modifier = Modifier.weight(1f),
            )
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
            style = BoardGameTypography.LabelCaps,
            color = BoardGameColors.OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(BoardGameColors.SurfaceContainerLowest)
                    .border(1.dp, BoardGameColors.OutlineVariant, BoardGameShapes.Medium)
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    value.ifBlank { placeholder },
                    style = BoardGameTypography.BodyLg,
                    color = if (value.isBlank()) BoardGameColors.Outline else BoardGameColors.OnSurface,
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = BoardGameColors.OnSurfaceVariant,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
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
    BgListCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Thumbnail placeholder (rounded)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(BoardGameColors.SurfaceContainerHigh),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    match.name,
                    style = BoardGameTypography.TitleLg,
                    color = BoardGameColors.OnSurface,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BgChip(match.category, BgChipStyle.CATEGORY)
                    BgChip(match.year, BgChipStyle.YEAR)
                }
            }
            IconButton(onClick = onAdd) {
                Icon(
                    Icons.Default.AddCircleOutline,
                    contentDescription = "Add ${match.name}",
                    tint = BoardGameColors.Primary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Preview(name = "Add Game — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameNewScreenPreview() {
    BoardGameTheme {
        AddGameNewScreenContent(state = AddGameNewState())
    }
}
