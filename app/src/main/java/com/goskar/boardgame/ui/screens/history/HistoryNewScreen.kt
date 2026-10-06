package com.goskar.boardgame.ui.screens.history

import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.gamesHistory.HistorySession
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.sessionDetails.SessionDetailsNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryState
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppFab
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import org.koin.androidx.compose.koinViewModel

class HistoryNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: GamesHistoryViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current

        HistoryNewScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onQueryChange = viewModel::updateSearchTxt,
            onSessionClick = { session -> navigator?.push(SessionDetailsNewScreen(session.id)) },
            onAddSession = { navigator?.push(AddGameplayNewScreen()) },
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
        )
    }
}

@Composable
fun HistoryNewScreenContent(
    state: GamesHistoryState,
    userInitials: String = "AM",
    onQueryChange: (String) -> Unit = {},
    onSelectFilter: (Int) -> Unit = {},
    onSessionClick: (HistorySession) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onAddSession: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) }

    AppScaffold(
        title = stringResource(R.string.history_game_screen),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = {
            AppAvatar(
                initials = userInitials,
                size = 36.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
            )
        },
        floatingActionButton = {
            AppFab(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Add session")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            AppSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.history_search_hint),
            )

            state.groups.forEach { group ->
                Spacer(Modifier.height(4.dp))
                GroupHeader(
                    title = group.title,
                    count = "${group.sessions.size} Session${if (group.sessions.size > 1) "s" else ""}",
                )
                group.sessions.forEach { session ->
                    SessionCard(session = session, onClick = { onSessionClick(session) })
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                state.showingLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.historyList.isEmpty() && !state.loading) {
                AppSecondaryButton(
                    text = stringResource(R.string.home_add_gameplay),
                    onClick = onAddSession,
                    modifier = Modifier.padding(horizontal = 48.dp),
                )
            }
            Spacer(Modifier.height(72.dp))
        }
    }
}

@Composable
private fun GroupHeader(title: String, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            count,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SessionCard(session: HistorySession, onClick: () -> Unit) {
    AppListCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (session.uri.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(session.uri.toUri())
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        session.gameName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (session.durationMin > 0) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${session.durationMin}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(16.dp))
                    }
                    Row(
                        modifier = Modifier.offset(x = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy((-8).dp),
                    ) {
                        session.avatars.forEach { initials ->
                            AppAvatar(initials = initials, size = 24.dp)
                        }
                        if (session.extraPlayers > 0) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "+${session.extraPlayers}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "WINNER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    session.winner,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
