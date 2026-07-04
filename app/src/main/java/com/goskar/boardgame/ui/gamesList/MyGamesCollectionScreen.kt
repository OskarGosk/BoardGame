package com.goskar.boardgame.ui.gamesList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.gamesList.lists.GameListState
import com.goskar.boardgame.ui.gamesList.lists.GameListViewModel
import com.goskar.boardgame.ui.gamesList.lists.GameUiState
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri
import com.goskar.boardgame.R

/**
 * "My Collection" screen (theme-aware) — replaces the old game-list view.
 * Built from the newDesign / App* components.
 */
class MyGamesCollectionScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel: GameListViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        MyGamesCollectionView(
            state = state,
            updateSearchTxt = viewModel::updateSearchTxt,
            updateCheckboxExpansionGame = viewModel::updateCheckboxExpansionGame,
            updateCheckboxBaseGame = viewModel::updateCheckboxBaseGame,
            useAllGameFilter = viewModel::useAllGameFilter
        )
    }
}

@Composable
fun MyGamesCollectionView(
    modifier: Modifier = Modifier,
    state: GameListState = GameListState(),
    updateSearchTxt: (String) -> Unit = {},
    updateCheckboxExpansionGame: () -> Unit = {},
    updateCheckboxBaseGame: () -> Unit = {},
    useAllGameFilter: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(1) } // Collection

    AppScaffold(
        title = stringResource(R.string.app_name_title),
        navItems = appNavItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        trailing = {
            AppAvatar(size = 36.dp, initials = "GK")
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
                            text = "My Collection",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = state.gameList?.size.toString() + " games",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    AppSearchBar(
                        value = state.searchTxt,
                        onValueChange = { updateSearchTxt(it) },
                        placeholder = "Search..."
                    )
                }

//                item {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            imageVector = Icons.Default.Star,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            text = "Featured Favorites",
//                            style = MaterialTheme.typography.titleLarge,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }

//                items(featuredGamesDummy) { game ->
//                    AppHeroCard(
//                        title = game.title,
//                        subtitle = game.subtitle,
//                        badge = game.badge,
//                        badgeStyle = AppChipStyle.STATUS_PLACE,
//                        onClick = {},
//                        imageContent = {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(game.color.copy(alpha = 0.5f))
//                            )
//                        }
//                    )
//                }

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
                                        if(label == "Base") updateCheckboxBaseGame()
                                        if(label == "Expansions") updateCheckboxExpansionGame()
                                        if(label == "All") useAllGameFilter()
                                    }
                                )
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                                CollectionGridItem(game, Modifier.weight(1f))
                            } else {
                                AddGridItem(Modifier.weight(1f))
                            }
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
            ) {
                AppFab(
                    onClick = {},
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Game") }
                )
            }
        }
    }
}

@Composable
fun CollectionGridItem(game: GameUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            val gameUri = game.game.uriFromBgg?:game.game.uri
            if(!gameUri.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(gameUri.toUri())
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
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
        Spacer(Modifier.height(8.dp))
        Text(
            text = game.game.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun AddGridItem(modifier: Modifier = Modifier) {
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
            .clickable { },
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
                text = "Add",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Models and dummy data ---

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
