package com.goskar.boardgame.ui.screens.collection

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.screens.addGame.AddGameNewScreen
import com.goskar.boardgame.ui.screens.gameDetails.GameDetailsNewScreen
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.components.other.SimpleAlertDialog
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.gamesList.lists.GameListState
import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.lists.GameUiState
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.utils.SortList
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppFab
import com.goskar.boardgame.ui.components.AppFilterChip
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppSearchBar

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
                game.game.bggId?.let { id -> navigator?.push(GameDetailsNewScreen(id, game.game.name)) }
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

    _root_ide_package_.com.goskar.boardgame.ui.navigation.AppScaffold(
        title = stringResource(R.string.app_name_title),
        navItems = appNavItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        trailing = {
            AppAvatar(
                size = 36.dp,
                initials = userInitials,
                modifier = Modifier.clip(CircleShape).clickable { onProfileClick() },
            )
        },
        floatingActionButton = {
            AppFab(
                onClick = onAddGame,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Game") }
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
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = stringResource(R.string.collection_title),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                R.string.collection_games_count,
                                state.gameList?.size ?: 0
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                            listOf("All", "Base", "Expansions").forEachIndexed { index, label ->
                                AppFilterChip(
                                    text = label,
                                    selected = selectedFilter == index,
                                    onToggle = {
                                        selectedFilter = index
                                        if (label == "Base") updateCheckboxBaseGame()
                                        if (label == "Expansions") updateCheckboxExpansionGame()
                                        if (label == "All") useAllGameFilter()
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

@Composable
private fun SortMenu(current: SortList, onSort: (SortList) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Sort",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortList.entries.forEach { sort ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(sort.value),
                            fontWeight = if (sort == current) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (sort == current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onSort(sort)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CollectionGridItem(
    game: GameUiState,
    modifier: Modifier = Modifier,
    onInfo: (() -> Unit)? = null,
    onAddSession: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val hasCover = !(game.game.uriFromBgg ?: game.game.uri).isNullOrBlank()
    // no cover -> start on the info side so the tile is never an empty box
    var flipped by remember { mutableStateOf(!hasCover) }
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, label = "flip")

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable { flipped = !flipped },
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                GameCoverFront(game)
            } else {
                Box(modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) {
                    GameCoverBack(game, onInfo, onAddSession, onEdit, onDelete)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = game.game.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        val category = game.game.category
        val year = game.game.yearPublished
        val rating = game.game.rating
        if (!category.isNullOrBlank() || year != null || rating != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!category.isNullOrBlank()) {
                    AppChip(category, AppChipStyle.CATEGORY)
                }
                if (year != null) {
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                if (rating != null) {
                    Text(
                        text = "★ " + String.format(java.util.Locale.US, "%.1f", rating),
                        style = MaterialTheme.typography.labelSmall,
                        color = appExt().success,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun GameCoverFront(game: GameUiState) {
    val gameUri = game.game.uriFromBgg ?: game.game.uri
    if (!gameUri.isNullOrBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(gameUri.toUri()).build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(BoardGameShapes.Small)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${game.game.games} plays",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.White
                )
            }
        }
    } else {
        // no cover: show basic info instead of an empty box
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            val players = playersLabel(game)
            if (players.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(players, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(2.dp))
            Text("${game.game.games} plays", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GameCoverBack(
    game: GameUiState,
    onInfo: (() -> Unit)?,
    onAddSession: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (game.game.expansion) {
            Text(
                stringResource(R.string.board_expansion),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
        }
        InfoLine(stringResource(R.string.collection_min_players), game.game.minPlayer.ifBlank { "—" })
        InfoLine(stringResource(R.string.collection_max_players), game.game.maxPlayer.ifBlank { "—" })
        InfoLine(stringResource(R.string.collection_games_played), game.game.games.toString())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            BackIcon(Icons.Default.Add, "Add session", MaterialTheme.colorScheme.primary, onAddSession)
            BackIcon(Icons.Default.Edit, "Edit", MaterialTheme.colorScheme.onSurfaceVariant, onEdit)
            if (onInfo != null) {
                BackIcon(Icons.Default.Info, "Info", MaterialTheme.colorScheme.primary, onInfo)
            }
            BackIcon(Icons.Default.Delete, "Delete", MaterialTheme.colorScheme.error, onDelete)
        }
    }
}

private fun playersLabel(game: GameUiState): String {
    val min = game.game.minPlayer.trim()
    val max = game.game.maxPlayer.trim()
    return when {
        min.isBlank() && max.isBlank() -> ""
        min.isBlank() -> "$max players"
        max.isBlank() || min == max -> "$min players"
        else -> "$min–$max players"
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        Text(
            value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BackIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(4.dp)
            .size(20.dp)
    )
}

@Composable
fun AddGridItem(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val strokeColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .aspectRatio(0.8f)
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.collection_add),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


data class GameData(
    val title: String,
    val subtitle: String = "",
    val badge: String = "",
    val plays: Int = 0,
    val color: Color = Color.Gray
)

private val featuredGamesDummy = listOf(
    GameData(
        "Ironwood Chronicles",
        "12 plays this month",
        "Most Played",
        color = Color(0xFFD2B48C)
    ),
    GameData("Neon Protocol", "New expansion added", "Hot Right Now", color = Color(0xFFADD8E6)),
    GameData(
        "Mythos of the Deep",
        "9.8/10 average score",
        "Highest Rated",
        color = Color(0xFFE6E6FA)
    )
)


@Preview(name = "My Collection — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun MyGamesCollectionLightPreview() {
    BoardGameTheme(darkTheme = false) { MyGamesCollectionView() }
}

@Preview(name = "My Collection — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun MyGamesCollectionDarkPreview() {
    BoardGameTheme(darkTheme = true) { MyGamesCollectionView() }
}
