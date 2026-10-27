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
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewEvent
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewState
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class GameDetailsNewScreen(
    private val bggId: String,
    private val name: String,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) { viewModel.load(bggId, name) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is GameDetailsNewEvent.Added -> {
                        snackbarHostState.show(resources.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is GameDetailsNewEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)
                }
            }
        }

        GameDetailsNewContent(
            state = state,
            onBack = { navigator?.pop() },
            onAdd = viewModel::addToCollection,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameDetailsNewContent(
    state: GameDetailsNewState,
    onBack: () -> Unit = {},
    onAdd: () -> Unit = {},
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

                if (!state.alreadyInCollection) {
                    AppPrimaryButton(text = stringResource(R.string.gd_add), onClick = onAdd)
                }
                AppPoweredByBgg()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


private val PreviewDetails = GameDetailsNewState(
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
private fun GameDetailsNewPreviewContent() {
    GameDetailsNewContent(state = PreviewDetails)
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GameDetailsNewLight() = BoardGameTheme(darkTheme = false) { GameDetailsNewPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GameDetailsNewDark() = BoardGameTheme(darkTheme = true) { GameDetailsNewPreviewContent() }
