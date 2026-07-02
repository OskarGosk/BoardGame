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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.ui.theme.*

/**
 * Nowy widok "My Collection" zastępujący stary widok listy gier.
 * Zbudowany na podstawie przesłanego projektu z wykorzystaniem komponentów newDesign.
 */
class MyGamesCollectionScreen : Screen {
    @Composable
    override fun Content() {
        MyGamesCollectionView()
    }
}

@Composable
fun MyGamesCollectionView() {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(1) } // Indeks 1: Collection

    val navItems = listOf(
        BgNavItem("Home", Icons.Default.Home),
        BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
        BgNavItem("Add Session", Icons.Default.AddCircle),
        BgNavItem("Players", Icons.Default.Person),
    )

    AppScaffold(
        title = "Tabletop Tracker",
        navItems = navItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        darkTheme = false,
        leading = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = BoardGameColors.Secondary
                )
            }
        },
        trailing = {
            // Profilowe w prawym górnym rogu
            BgAvatar(size = 36.dp, initials = "GK")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BoardGameColors.Background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tytuł i statystyka
                item {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = "My Collection",
                            style = BoardGameTypography.DisplayLgMobile,
                            color = BoardGameColors.OnSurface
                        )
                        Text(
                            text = "48 games in your library",
                            style = BoardGameTypography.BodySm,
                            color = BoardGameColors.OnSurfaceVariant
                        )
                    }
                }

                // Wyszukiwarka - BgSearchBar z newDesign
                item {
                    BgSearchBar(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = "Search..."
                    )
                }

                // Sekcja Featured
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BoardGameColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Featured Favorites",
                            style = BoardGameTypography.TitleLg,
                            color = BoardGameColors.Primary
                        )
                    }
                }

                // Hero Cards
                items(featuredGamesDummy) { game ->
                    BgHeroCard(
                        title = game.title,
                        subtitle = game.subtitle,
                        badge = game.badge,
                        badgeStyle = BgChipStyle.STATUS_PLACE, // Styl zbliżony do projektu
                        onClick = {},
                        imageContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(game.color.copy(alpha = 0.5f))
                            )
                        }
                    )
                }

                // Filtry i Sortowanie
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("All", "Base", "Expansions").forEachIndexed { index, label ->
                                BgFilterChip(
                                    text = label,
                                    selected = selectedFilter == index,
                                    onToggle = { selectedFilter = index }
                                )
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Sort",
                                tint = BoardGameColors.Primary
                            )
                        }
                    }
                }

                // Grid z grami i przyciskiem Add
                val gridItems = gamesGridDummy + listOf(null)
                val rows = gridItems.chunked(2)

                items(rows) { rowItems ->
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

            // FAB
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
            ) {
                BgFab(
                    onClick = {},
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Game") }
                )
            }
        }
    }
}

@Composable
fun CollectionGridItem(game: GameData, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .clip(BoardGameShapes.ExtraLarge)
                .background(game.color.copy(alpha = 0.4f))
        ) {
            // Etykieta plays na dole obrazka
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(BoardGameShapes.Small)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${game.plays} plays",
                    style = BoardGameTypography.LabelCaps.copy(fontSize = 10.sp),
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = game.title,
            style = BoardGameTypography.BodySm.copy(fontWeight = FontWeight.SemiBold),
            color = BoardGameColors.OnSurface,
            maxLines = 1
        )
    }
}

@Composable
fun AddGridItem(modifier: Modifier = Modifier) {
    val strokeColor = BoardGameColors.OutlineVariant
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
                tint = BoardGameColors.Primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Add",
                style = BoardGameTypography.BodySm,
                color = BoardGameColors.OnSurfaceVariant
            )
        }
    }
}

// --- Modele i dane testowe ---

data class GameData(
    val title: String,
    val subtitle: String = "",
    val badge: String = "",
    val plays: Int = 0,
    val color: Color = Color.Gray
)

private val featuredGamesDummy = listOf(
    GameData("Ironwood Chronicles", "12 plays this month", "Most Played", color = Color(0xFFD2B48C)),
    GameData("Neon Protocol", "New expansion added", "Hot Right Now", color = Color(0xFFADD8E6)),
    GameData("Mythos of the Deep", "9.8/10 average score", "Highest Rated", color = Color(0xFFE6E6FA))
)

private val gamesGridDummy = listOf(
    GameData("Galactic Reach", plays = 4, color = Color(0xFFB0C4DE)),
    GameData("Ironwood", plays = 12, color = Color(0xFFD2B48C)),
    GameData("Neon Protocol", plays = 28, color = Color(0xFFADD8E6))
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun MyGamesCollectionPreview() {
    BoardGameTheme(darkTheme = false) {
        MyGamesCollectionView()
    }
}
