package com.goskar.boardgame.ui.screens.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppFab
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.AppSelectChip
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.components.other.SimpleAlertDialog
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameListState
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameListViewModel
import com.goskar.boardgame.ui.screens.collection.viewmodel.GameUiState
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addGame.AddGameNewScreen
import com.goskar.boardgame.ui.screens.collection.components.AddGridItem
import com.goskar.boardgame.ui.screens.collection.components.CollectionGridItem
import com.goskar.boardgame.ui.screens.collection.components.SortMenu
import com.goskar.boardgame.ui.screens.gameDetails.GameDetailsNewScreen
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.utils.SortList
import org.koin.androidx.compose.koinViewModel

class MyGamesCollectionScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel: GameListViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        MyGamesCollectionView(
            state = state,
            userInitials = rememberUserInitials(),
            updateSearchTxt = viewModel::updateSearchTxt,
            updateCheckboxExpansionGame = viewModel::updateCheckboxExpansionGame,
            updateCheckboxBaseGame = viewModel::updateCheckboxBaseGame,
            useAllGameFilter = viewModel::useAllGameFilter,
            onSort = viewModel::updateSortOption,
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
            onAddGame = { navigator?.push(AddGameNewScreen()) },
            onOpenGame = { game ->
                game.game.bggId?.let { id ->
                    navigator?.push(
                        GameDetailsNewScreen(
                            id,
                            game.game.name
                        )
                    )
                }
            },
            onAddSession = { game ->
                navigator?.push(AddGameplayNewScreen(preselectedGameId = game.game.id))
            },
            onEditGame = { game -> navigator?.push(AddGameNewScreen(editGameId = game.game.id)) },
            onDeleteGame = { game -> viewModel.validateDeleteGame(game.game) }
        )
    }
}

@Composable
fun MyGamesCollectionView(
    modifier: Modifier = Modifier,
    state: GameListState = GameListState(),
    userInitials: String = "AM",
    updateSearchTxt: (String) -> Unit = {},
    updateCheckboxExpansionGame: () -> Unit = {},
    updateCheckboxBaseGame: () -> Unit = {},
    useAllGameFilter: () -> Unit = {},
    onSort: (SortList) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddGame: () -> Unit = {},
    onOpenGame: (GameUiState) -> Unit = {},
    onAddSession: (GameUiState) -> Unit = {},
    onEditGame: (GameUiState) -> Unit = {},
    onDeleteGame: (GameUiState) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(1) }
    var gameToDelete by remember { mutableStateOf<GameUiState?>(null) }

    AppScaffold(
        title = stringResource(R.string.collection_title),
        subtitle = stringResource(
            R.string.collection_games_count,
            state.gameList?.size ?: 0
        ),
        navItems = appNavItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        trailing = {
            AppAvatar(
                size = 36.dp,
                initials = userInitials,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
            )
        },
        floatingActionButton = {
            AppFab(
                onClick = onAddGame,
                icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.addgame_title)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    AppSearchBar(
                        value = state.searchTxt,
                        onValueChange = { updateSearchTxt(it) },
                        placeholder = stringResource(R.string.collection_search_hint)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                R.string.collection_filter_all,
                                R.string.collection_filter_base,
                                R.string.collection_filter_expansions,
                            ).forEachIndexed { index, labelRes ->
                                AppSelectChip(
                                    text = stringResource(labelRes),
                                    selected = selectedFilter == index,
                                    onToggle = {
                                        selectedFilter = index
                                        when (labelRes) {
                                            R.string.collection_filter_base -> updateCheckboxBaseGame()
                                            R.string.collection_filter_expansions -> updateCheckboxExpansionGame()
                                            R.string.collection_filter_all -> useAllGameFilter()
                                        }
                                    }
                                )
                            }
                        }
                        SortMenu(current = state.sortOption, onSort = onSort)
                    }
                }

                val gridItems = state.gameListEdited + listOf(null)
                val rows = gridItems.chunked(2)

                items(items = rows) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { game ->
                            if (game != null) {
                                CollectionGridItem(
                                    game = game,
                                    modifier = Modifier.weight(1f),
                                    onInfo = if (game.game.bggId != null) ({ onOpenGame(game) }) else null,
                                    onAddSession = { onAddSession(game) },
                                    onEdit = { onEditGame(game) },
                                    onDelete = { gameToDelete = game },
                                )
                            } else {
                                AddGridItem(Modifier.weight(1f), onClick = onAddGame)
                            }
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                item { AppPoweredByBgg() }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    gameToDelete?.let { target ->
        SimpleAlertDialog(
            titleText = stringResource(R.string.delete, target.game.name),
            contentText = R.string.board_delete_info,
            onDismiss = { gameToDelete = null },
            confirmButtonClick = {
                onDeleteGame(target)
                gameToDelete = null
            }
        )
    }
}

@Preview(name = "My Collection — No Covers", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun MyGamesCollectionNoCoverPreview() {
    val noCoverGame1 = GameUiState(
        game = Game(
            name = "Chess",
            expansion = false,
            cooperate = false,
            baseGame = "",
            minPlayer = "2",
            maxPlayer = "2",
            games = 5,
            category = "Abstract",
            yearPublished = 1475,
            rating = 8.0,
            bggId = "171",
        )
    )
    val noCoverGame2 = GameUiState(
        game = Game(
            name = "Checkers",
            expansion = false,
            cooperate = false,
            baseGame = "",
            minPlayer = "2",
            maxPlayer = "2",
            games = 10,
            category = "Abstract",
            yearPublished = 1100,
            rating = 7.0,
        )
    )

    val state = GameListState(
        gameList = listOf(noCoverGame1, noCoverGame2),
        gameListEdited = listOf(noCoverGame1, noCoverGame2)
    )

    BoardGameTheme {
        MyGamesCollectionView(state = state)
    }
}

@Preview(name = "My Collection — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun MyGamesCollectionLightPreview() {
    BoardGameTheme(darkTheme = false) { MyGamesCollectionView(
    ) }
}

@Preview(name = "My Collection — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun MyGamesCollectionDarkPreview() {
    BoardGameTheme(darkTheme = true) { MyGamesCollectionView() }
}
