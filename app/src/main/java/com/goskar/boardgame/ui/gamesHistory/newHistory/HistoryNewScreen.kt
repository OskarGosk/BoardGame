package com.goskar.boardgame.ui.gamesHistory.newHistory

import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.gamesHistory.newSessionDetails.SessionDetailsNewScreen
import com.goskar.boardgame.ui.theme.BgDarkAvatar
import com.goskar.boardgame.ui.theme.BgDarkBottomNavBar
import com.goskar.boardgame.ui.theme.BgDarkChip
import com.goskar.boardgame.ui.theme.BgDarkFab
import com.goskar.boardgame.ui.theme.BgDarkFilterChip
import com.goskar.boardgame.ui.theme.BgDarkListCard
import com.goskar.boardgame.ui.theme.BgDarkSearchBar
import com.goskar.boardgame.ui.theme.BgDarkSecondaryButton
import com.goskar.boardgame.ui.theme.BgDarkTopBar
import com.goskar.boardgame.ui.theme.BgNavItem
import com.goskar.boardgame.ui.theme.BoardGameDarkColors
import com.goskar.boardgame.ui.theme.BoardGameDarkSpacing
import com.goskar.boardgame.ui.theme.BoardGameDarkTypography
import com.goskar.boardgame.ui.theme.BoardGameShapes
import org.koin.androidx.compose.koinViewModel

private val darkNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
    BgNavItem("Add Session", Icons.Default.Add),
    BgNavItem("Players", Icons.Default.Person),
)

/**
 * New "Gaming History" screen (dark redesign). Backed by [HistoryNewViewModel]
 * (placeholder state only).
 */
class HistoryNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: HistoryNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        HistoryNewScreenContent(
            state = state,
            onQueryChange = viewModel::updateQuery,
            onSelectFilter = viewModel::selectFilter,
            onSessionClick = { navigator?.push(SessionDetailsNewScreen()) },
        )
    }
}

@Composable
fun HistoryNewScreenContent(
    state: HistoryNewState,
    onMenu: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSelectFilter: (Int) -> Unit = {},
    onSessionClick: (HistorySession) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onAddSession: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) } // Collection tab

    Scaffold(
        containerColor = BoardGameDarkColors.Background,
        topBar = {
            BgDarkTopBar(
                title = "Tabletop Tracker",
                showMenu = true,
                onMenuClick = onMenu,
                trailingContent = { BgDarkAvatar(initials = "AM", size = 36.dp) },
            )
        },
        bottomBar = {
            BgDarkBottomNavBar(darkNavItems, selectedNav, { selectedNav = it })
        },
        floatingActionButton = {
            BgDarkFab(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Add session")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BoardGameDarkSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                "Gaming History",
                style = BoardGameDarkTypography.HeadlineLgMobile,
                color = BoardGameDarkColors.OnSurface,
            )

            BgDarkSearchBar(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = "Search sessions…",
            )

            // Filter chips
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.filters.forEachIndexed { index, filter ->
                    BgDarkFilterChip(
                        text = filter,
                        selected = index == state.selectedFilter,
                        onToggle = { onSelectFilter(index) },
                    )
                }
            }

            // Groups
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
                style = BoardGameDarkTypography.BodyMd,
                color = BoardGameDarkColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            BgDarkSecondaryButton(
                text = "Load More",
                onClick = onLoadMore,
                modifier = Modifier.padding(horizontal = 48.dp),
            )
            Spacer(Modifier.height(72.dp)) // room for FAB
        }
    }
}

@Composable
private fun GroupHeader(title: String, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            title.uppercase(),
            style = BoardGameDarkTypography.LabelLg,
            color = BoardGameDarkColors.Primary,
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BoardGameDarkColors.CardBorder,
            thickness = 0.5.dp,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            count,
            style = BoardGameDarkTypography.LabelMd,
            color = BoardGameDarkColors.OnSurfaceVariant,
        )
    }
}

@Composable
private fun SessionCard(session: HistorySession, onClick: () -> Unit) {
    BgDarkListCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(BoardGameDarkColors.SurfaceContainerHigh),
            )
            Spacer(Modifier.width(12.dp))

            // Title + meta
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        session.gameName,
                        style = BoardGameDarkTypography.TitleLg,
                        color = BoardGameDarkColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    BgDarkChip(session.category, session.categoryStyle)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = BoardGameDarkColors.OnSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${session.durationMin}m",
                        style = BoardGameDarkTypography.BodyMd,
                        color = BoardGameDarkColors.OnSurfaceVariant,
                    )
                    Spacer(Modifier.width(10.dp))
                    PlayerAvatars(session.avatars, session.extraPlayers)
                }
            }

            Spacer(Modifier.width(8.dp))

            // Winner
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "WINNER",
                    style = BoardGameDarkTypography.LabelMd,
                    color = BoardGameDarkColors.OnSurfaceVariant,
                )
                Text(
                    session.winner,
                    style = BoardGameDarkTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold),
                    color = if (session.winnerIsYou) BoardGameDarkColors.Primary else BoardGameDarkColors.Success,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = BoardGameDarkColors.Outline,
            )
        }
    }
}

@Composable
private fun PlayerAvatars(avatars: List<String>, extra: Int) {
    Row {
        avatars.forEachIndexed { index, initials ->
            BgDarkAvatar(
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
                    .background(BoardGameDarkColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "+$extra",
                    style = BoardGameDarkTypography.LabelMd,
                    color = BoardGameDarkColors.OnSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Gaming History — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun HistoryNewScreenPreview() {
    HistoryNewScreenContent(state = HistoryNewState())
}
