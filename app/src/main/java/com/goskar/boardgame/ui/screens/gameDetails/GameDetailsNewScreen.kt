package com.goskar.boardgame.ui.screens.gameDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewEvent
import com.goskar.boardgame.ui.screens.gameDetails.viewmodel.GameDetailsNewState
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppPoweredByBgg
import com.goskar.boardgame.ui.theme.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.appExt
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
        val context = LocalContext.current

        LaunchedEffect(Unit) { viewModel.load(bggId, name) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is GameDetailsNewEvent.Added -> {
                        snackbarHostState.show(context.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is GameDetailsNewEvent.ShowMessage ->
                        snackbarHostState.show(context.getString(event.message), event.type)
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
                CoverImage(state.imageUrl)

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

                StatsCard(state)

                if (state.categories.isNotEmpty()) {
                    AppSectionHeader(title = stringResource(R.string.gd_categories))
                    ChipFlow(state.categories, AppChipStyle.CATEGORY)
                }

                if (state.mechanics.isNotEmpty()) {
                    AppSectionHeader(title = stringResource(R.string.gd_mechanics))
                    ChipFlow(state.mechanics, AppChipStyle.EXPANSION)
                }

                if (state.designers.isNotEmpty()) {
                    CreditRow(stringResource(R.string.gd_designers), state.designers.joinToString(", "))
                }
                if (state.publishers.isNotEmpty()) {
                    CreditRow(stringResource(R.string.gd_publisher), state.publishers.joinToString(", "))
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

@Composable
private fun CoverImage(url: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(BoardGameShapes.ExtraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(url.toUri()).build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun StatsCard(state: GameDetailsNewState) {
    AppListCard {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailStat(stringResource(R.string.gd_players), state.players.substringBefore(" ").ifBlank { "—" }, Modifier.weight(1f))
            CellDivider()
            DetailStat(stringResource(R.string.gd_time), state.playtime.ifBlank { "—" }, Modifier.weight(1f))
            CellDivider()
            DetailStat(stringResource(R.string.gd_age), state.age.ifBlank { "—" }, Modifier.weight(1f))
        }
        if (state.rating.isNotBlank() || state.weight.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DetailStat(
                    stringResource(R.string.gd_rating),
                    state.rating.ifBlank { "—" },
                    Modifier.weight(1f),
                    valueColor = appExt().success,
                )
                CellDivider()
                DetailStat(stringResource(R.string.gd_weight), state.weight.ifBlank { "—" }, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor)
    }
}

@Composable
private fun CellDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(32.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(items: List<String>, style: AppChipStyle) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { AppChip(it, style) }
    }
}

@Composable
private fun CreditRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
