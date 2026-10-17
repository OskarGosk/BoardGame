package com.goskar.boardgame.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppFab
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryState
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.gamesHistory.HistoryGroup
import com.goskar.boardgame.ui.gamesHistory.HistorySession
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.history.components.GroupHeader
import com.goskar.boardgame.ui.screens.history.components.SessionCard
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.screens.sessionDetails.SessionDetailsNewScreen
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_session))
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
                    count = pluralStringResource(R.plurals.history_sessions_count, group.sessions.size, group.sessions.size),
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


private val PreviewSession = HistorySession(
    id = "1",
    gameName = "Dune: Imperium",
    durationMin = 90,
    avatars = listOf("AM", "SK"),
    extraPlayers = 2,
    winner = "Alex M.",
    winnerIsYou = true,
    uri = "",
)

@Composable
private fun HistoryNewPreviewContent() {
    HistoryNewScreenContent(
        state = GamesHistoryState(
            loading = false,
            showingLabel = "Showing 1 session",
            groups = listOf(HistoryGroup(title = "This Week", sessions = listOf(PreviewSession))),
        ),
    )
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun HistoryNewLight() = BoardGameTheme(darkTheme = false) { HistoryNewPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HistoryNewDark() = BoardGameTheme(darkTheme = true) { HistoryNewPreviewContent() }
