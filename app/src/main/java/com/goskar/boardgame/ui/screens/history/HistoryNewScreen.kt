package com.goskar.boardgame.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryState
import com.goskar.boardgame.ui.gamesHistory.GamesHistoryViewModel
import com.goskar.boardgame.ui.screens.logGameplay.AddGameplayNewScreen
import com.goskar.boardgame.ui.screens.sessionDetails.SessionDetailsNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppChip
import com.goskar.boardgame.ui.theme.AppChipStyle
import java.time.LocalDate
import com.goskar.boardgame.ui.theme.AppFab
import com.goskar.boardgame.ui.theme.AppFilterChip
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSearchBar
import com.goskar.boardgame.ui.theme.AppSecondaryButton
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.appExt
import org.koin.androidx.compose.koinViewModel

data class HistorySession(
    val gameName: String,
    val category: String,
    val categoryStyle: AppChipStyle,
    val durationMin: Int,
    val avatars: List<String>,
    val extraPlayers: Int,
    val winner: String,
    val winnerIsYou: Boolean = false,
)

data class HistoryGroup(val title: String, val sessions: List<HistorySession>)

data class HistoryNewState(
    val query: String = "",
    val filters: List<String> = listOf("All Games", "Wins Only", "Strategy", "Co-op"),
    val selectedFilter: Int = 0,
    val showingLabel: String = "",
    val groups: List<HistoryGroup> = emptyList(),
)

class HistoryNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: GamesHistoryViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        HistoryNewScreenContent(
            state = state.toHistoryNewState(),
            onQueryChange = viewModel::updateSearchTxt,
            onSessionClick = { navigator?.push(SessionDetailsNewScreen()) },
            onAddSession = { navigator?.push(AddGameplayNewScreen()) },
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
        )
    }
}

private fun GamesHistoryState.toHistoryNewState(): HistoryNewState {
    val q = searchTxt.trim()
    val filtered = if (q.isBlank()) historyList
    else historyList.filter { it.gameName.contains(q, true) || it.winner.contains(q, true) }
    val sorted = filtered.sortedByDescending { it.gameData }
    val today = LocalDate.now()
    val byBucket = sorted.groupBy { bucketLabel(it.gameData, today) }
    val groups = listOf("Today", "Yesterday", "This Week", "Earlier").mapNotNull { label ->
        byBucket[label]?.let { HistoryGroup(label, it.map { g -> g.toSession() }) }
    }
    return HistoryNewState(
        query = searchTxt,
        showingLabel = "Showing ${filtered.size} session${if (filtered.size != 1) "s" else ""}",
        groups = groups,
    )
}

private fun bucketLabel(date: LocalDate, today: LocalDate): String = when {
    date == today -> "Today"
    date == today.minusDays(1) -> "Yesterday"
    date.isAfter(today.minusDays(7)) -> "This Week"
    else -> "Earlier"
}

private fun HistoryGame.toSession(): HistorySession = HistorySession(
    gameName = gameName,
    category = "",
    categoryStyle = AppChipStyle.CATEGORY,
    durationMin = 0,
    avatars = listOfPlayer.map { historyInitials(it) }.take(3),
    extraPlayers = (listOfPlayer.size - 3).coerceAtLeast(0),
    winner = winner,
)

private fun historyInitials(name: String): String =
    name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
        .joinToString("").ifBlank { name.take(2) }.uppercase()

@Composable
fun HistoryNewScreenContent(
    state: HistoryNewState,
    onQueryChange: (String) -> Unit = {},
    onSelectFilter: (Int) -> Unit = {},
    onSessionClick: (HistorySession) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onAddSession: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) }

    AppScaffold(
        title = "Tabletop Tracker",
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = {
            AppAvatar(
                initials = "AM",
                size = 36.dp,
                modifier = Modifier.clip(CircleShape).clickable { onProfileClick() },
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
            Text(
                "Gaming History",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            AppSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "Search sessions…",
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.filters.forEachIndexed { index, filter ->
                    AppFilterChip(
                        text = filter,
                        selected = index == state.selectedFilter,
                        onToggle = { onSelectFilter(index) },
                    )
                }
            }

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
            AppSecondaryButton(
                text = "Load More",
                onClick = onLoadMore,
                modifier = Modifier.padding(horizontal = 48.dp),
            )
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
            )
            Spacer(Modifier.width(12.dp))

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
                    if (session.category.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        AppChip(session.category, session.categoryStyle)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (session.durationMin > 0) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${session.durationMin}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                        Spacer(Modifier.width(10.dp))
                    }
                    PlayerAvatars(session.avatars, session.extraPlayers)
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "WINNER",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    session.winner,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (session.winnerIsYou) MaterialTheme.colorScheme.primary else appExt().success,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun PlayerAvatars(avatars: List<String>, extra: Int) {
    Row {
        avatars.forEachIndexed { index, initials ->
            AppAvatar(
                initials = initials,
                size = 24.dp,
                modifier = Modifier.offset(x = (index * -8).dp),
            )
        }
        if (extra > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (avatars.size * -8).dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "+$extra",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Gaming History — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun HistoryNewLightPreview() {
    BoardGameTheme(darkTheme = false) { HistoryNewScreenContent(state = HistoryNewState()) }
}

@Preview(name = "Gaming History — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HistoryNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { HistoryNewScreenContent(state = HistoryNewState()) }
}
