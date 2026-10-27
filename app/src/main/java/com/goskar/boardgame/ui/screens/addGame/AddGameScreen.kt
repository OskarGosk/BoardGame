package com.goskar.boardgame.ui.screens.addGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addGame.components.AppSegmentedControl
import com.goskar.boardgame.ui.screens.addGame.components.ManualEntryForm
import com.goskar.boardgame.ui.screens.addGame.components.PopularMatchCard
import com.goskar.boardgame.ui.screens.addGame.viewmodel.*
import com.goskar.boardgame.ui.screens.gameDetails.GameDetailsScreen
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class AddGameScreen(
    private val editGameId: String? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) {
            if (editGameId != null) viewModel.loadForEdit(editGameId)
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddGameEvent.Saved -> {
                        snackbarHostState.show(resources.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is AddGameEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)
                }
            }
        }

        AddGameScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onQueryChange = viewModel::updateQuery,
            onSearch = viewModel::search,
            onSelectTab = viewModel::selectTab,
            onAddGame = viewModel::addFromBgg,
            onOpenDetails = { match -> navigator?.push(GameDetailsScreen(match.id, match.name)) },
            onNameChange = viewModel::updateName,
            onMinChange = viewModel::updateMinPlayers,
            onMaxChange = viewModel::updateMaxPlayers,
            onToggleMode = viewModel::toggleMode,
            onToggleExpansion = viewModel::toggleExpansion,
            onSelectBaseGame = viewModel::selectBaseGame,
            onCategoryChange = viewModel::updateCategory,
            onYearChange = viewModel::updateYear,
            onCoverPicked = viewModel::updateCoverUri,
            onSaveManual = viewModel::saveManual,
        )
    }
}

@Composable
fun AddGameScreenContent(
    state: AddGameState,
    onBack: () -> Unit = {},
    onHelp: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    onAddGame: (PopularMatch) -> Unit = {},
    onOpenDetails: (PopularMatch) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onMinChange: (String) -> Unit = {},
    onMaxChange: (String) -> Unit = {},
    onToggleMode: (GameMode) -> Unit = {},
    onToggleExpansion: (Boolean) -> Unit = {},
    onSelectBaseGame: (String) -> Unit = {},
    onCategoryChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    onCoverPicked: (String) -> Unit = {},
    onSaveManual: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) }

    AppScaffold(
        title = if (state.isEditMode) stringResource(R.string.addgame_title_edit)
        else stringResource(R.string.addgame_title),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        onBack = onBack,
        trailing = {
            IconButton(onClick = onHelp) {
                Icon(Icons.Default.HelpOutline, contentDescription = stringResource(R.string.cd_help), tint = MaterialTheme.colorScheme.primary)
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

            if (!state.isEditMode) {
                AppSegmentedControl(
                    options = listOf(stringResource(R.string.addgame_tab_bgg), stringResource(R.string.addgame_tab_manual)),
                    selected = state.selectedTab,
                    onSelect = onSelectTab,
                )
            }

            if (state.selectedTab == 0 && !state.isEditMode) {
                AppSearchBar(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.addgame_search_hint),
                    onSearch = onSearch,
                )
                AppSectionHeader(title = stringResource(R.string.addgame_popular_matches))
                state.popularMatches.forEach { match ->
                    PopularMatchCard(
                        match = match,
                        onAdd = { onAddGame(match) },
                        onOpen = { onOpenDetails(match) },
                    )
                }
                AppPoweredByBgg()
            } else {
                ManualEntryForm(
                    state = state,
                    onNameChange = onNameChange,
                    onMinChange = onMinChange,
                    onMaxChange = onMaxChange,
                    onToggleMode = onToggleMode,
                    onToggleExpansion = onToggleExpansion,
                    onSelectBaseGame = onSelectBaseGame,
                    onCategoryChange = onCategoryChange,
                    onYearChange = onYearChange,
                    onCoverPicked = onCoverPicked,
                    onSave = onSaveManual,
                )
            }
        }
    }
}

@Preview(name = "Add Game — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameLightPreview() {
    BoardGameTheme(darkTheme = false) { AddGameScreenContent(state = AddGameState()) }
}

@Preview(name = "Add Game — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddGameDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddGameScreenContent(state = AddGameState()) }
}
