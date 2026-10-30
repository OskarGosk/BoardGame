package com.goskar.boardgame.ui.screens.gameDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.screens.gameDetails.components.ChipFlow
import com.goskar.boardgame.ui.screens.gameDetails.components.CoverImage
import com.goskar.boardgame.ui.screens.gameDetails.components.CreditRow
import com.goskar.boardgame.ui.screens.gameDetails.components.StatsCard
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsEvent
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsState
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class GameDetailsScreen(
    private val bggId: String,
    private val name: String,
    private val linkGameId: String? = null,
) : Screen {

    // Voyager keys screens by class by default; after linking, the preview is replaced by another
    // GameDetailsScreen, which would otherwise inherit the preview's composition and ViewModel.
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val viewModel: com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) { viewModel.load(bggId, name, linkGameId) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is GameDetailsEvent.Added -> {
                        snackbarHostState.show(resources.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is GameDetailsEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)

                    // Drop this preview and the BGG search under it, so Back leads straight to the collection.
                    is GameDetailsEvent.Linked -> {
                        snackbarHostState.show(resources.getString(event.message), event.type)
                        navigator?.pop()
                        navigator?.replace(GameDetailsScreen(event.bggId, event.gameName))
                    }
                }
            }
        }

        GameDetailsContent(
            state = state,
            onBack = { navigator?.pop() },
            onAdd = viewModel::addToCollection,
            onLink = viewModel::linkToGame,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameDetailsContent(
    state: GameDetailsState,
    onBack: () -> Unit = {},
    onAdd: () -> Unit = {},
    onLink: () -> Unit = {},
) {
    AppScaffold(
        title = stringResource(R.string.game_details_title),
        navItems = null,
        onBack = onBack,
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            state.isError -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.gd_error),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(BoardGameSpacing.MarginMobile),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CoverImage(url = state.imageUrl)

                Column {
                    Text(
                        state.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val subtitle = listOfNotNull(
                        state.year.ifBlank { null },
                        state.bestPlayers.ifBlank { null },
                    ).joinToString("  •  ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                StatsCard(state = state)

                if (state.categories.isNotEmpty()) {
                    AppSectionHeader(title = stringResource(R.string.gd_categories))
                    ChipFlow(items = state.categories, style = AppChipStyle.CATEGORY)
                }

                if (state.mechanics.isNotEmpty()) {
                    AppSectionHeader(title = stringResource(R.string.gd_mechanics))
                    ChipFlow(items = state.mechanics, style = AppChipStyle.EXPANSION)
                }

                if (state.designers.isNotEmpty()) {
                    CreditRow(label = stringResource(R.string.gd_designers), value = state.designers.joinToString(", "))
                }
                if (state.publishers.isNotEmpty()) {
                    CreditRow(label = stringResource(R.string.gd_publisher), value = state.publishers.joinToString(", "))
                }

                if (state.description.isNotBlank()) {
                    AppSectionHeader(title = stringResource(R.string.gd_description))
                    AppListCard {
                        Text(
                            state.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (state.linkGameName != null) {
                    Text(
                        stringResource(R.string.gd_link_question, state.linkGameName),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AppPrimaryButton(
                        text = stringResource(R.string.gd_link),
                        onClick = onLink,
                        loading = state.isLinking,
                        enabled = !state.isLinking,
                    )
                } else if (!state.alreadyInCollection) {
                    AppPrimaryButton(text = stringResource(R.string.gd_add), onClick = onAdd)
                }
                AppPoweredByBgg()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


private val PreviewDetails = GameDetailsState(
    name = "Dune: Imperium",
    year = "2020",
    players = "1–4",
    bestPlayers = "3",
    playtime = "60–120",
    age = "14+",
    rating = "8.4",
    weight = "3.0",
    description = "Influence, intrigue, and combat in the universe of Dune.",
    categories = listOf("Strategy", "Card Game"),
    mechanics = listOf("Deck Building", "Worker Placement"),
    designers = listOf("Paul Dennen"),
    publishers = listOf("Dire Wolf"),
)

@Composable
private fun GameDetailsPreviewContent() {
    GameDetailsContent(state = PreviewDetails)
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameDetailsLight() = BoardGameTheme(darkTheme = false) { GameDetailsPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameDetailsDark() = BoardGameTheme(darkTheme = true) { GameDetailsPreviewContent() }

@Preview(name = "Link mode", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameDetailsLinkMode() = BoardGameTheme(darkTheme = false) {
    GameDetailsContent(state = PreviewDetails.copy(linkGameName = "Diuna: Imperium"))
}
