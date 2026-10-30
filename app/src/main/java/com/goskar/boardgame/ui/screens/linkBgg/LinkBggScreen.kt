package com.goskar.boardgame.ui.screens.linkBgg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.screens.addGame.components.PopularMatchCard
import com.goskar.boardgame.ui.screens.addGame.viewmodel.PopularMatch
import com.goskar.boardgame.ui.screens.gameDetails.GameDetailsScreen
import com.goskar.boardgame.ui.screens.linkBgg.viewmodel.LinkBggEvent
import com.goskar.boardgame.ui.screens.linkBgg.viewmodel.LinkBggState
import com.goskar.boardgame.ui.screens.linkBgg.viewmodel.LinkBggViewModel
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class LinkBggScreen(
    private val gameId: String,
    private val gameName: String,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: LinkBggViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) { viewModel.start(gameName) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is LinkBggEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)
                }
            }
        }

        LinkBggContent(
            state = state,
            onBack = { navigator?.pop() },
            onQueryChange = viewModel::updateQuery,
            onSearch = viewModel::search,
            onOpenMatch = { match -> navigator?.push(GameDetailsScreen(match.id, match.name, linkGameId = gameId)) },
        )
    }
}

@Composable
fun LinkBggContent(
    state: LinkBggState,
    onBack: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    onOpenMatch: (PopularMatch) -> Unit = {},
) {
    AppScaffold(
        title = stringResource(R.string.link_bgg_title),
        navItems = null,
        onBack = onBack,
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
            Text(
                stringResource(R.string.link_bgg_intro, state.gameName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.addgame_search_hint),
                onSearch = onSearch,
            )

            when {
                state.isSearching -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                state.searched && state.matches.isEmpty() -> Text(
                    stringResource(R.string.link_bgg_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                else -> state.matches.forEach { match ->
                    PopularMatchCard(match = match, onAdd = null, onOpen = { onOpenMatch(match) })
                }
            }

            AppPoweredByBgg()
            Spacer(Modifier.height(8.dp))
        }
    }
}


private val PreviewState = LinkBggState(
    gameName = "Wirus!",
    query = "Wirus!",
    searched = true,
    matches = listOf(
        PopularMatch(id = "180263", name = "Virus!", category = "BGG", year = "2015"),
        PopularMatch(id = "1", name = "Virus", category = "BGG", year = "1995"),
    ),
)

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun LinkBggLight() = BoardGameTheme(darkTheme = false) { LinkBggContent(state = PreviewState) }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun LinkBggDark() = BoardGameTheme(darkTheme = true) { LinkBggContent(state = PreviewState) }

@Preview(name = "No results", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun LinkBggNoResults() = BoardGameTheme(darkTheme = false) {
    LinkBggContent(state = PreviewState.copy(matches = emptyList()))
}
