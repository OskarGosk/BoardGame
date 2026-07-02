package com.goskar.boardgame.ui.gamesHistory.newSessionDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.newDesign.BgDarkSectionHeader
import com.goskar.boardgame.ui.theme.BgDarkAvatar
import com.goskar.boardgame.ui.theme.BgDarkChip
import com.goskar.boardgame.ui.theme.BgDarkGhostButton
import com.goskar.boardgame.ui.theme.BgDarkListCard
import com.goskar.boardgame.ui.theme.BgDarkPrimaryButton
import com.goskar.boardgame.ui.theme.BgDarkTopBar
import com.goskar.boardgame.ui.theme.BoardGameDarkColors
import com.goskar.boardgame.ui.theme.BoardGameDarkSpacing
import com.goskar.boardgame.ui.theme.BoardGameDarkTypography
import com.goskar.boardgame.ui.theme.BoardGameShapes
import org.koin.androidx.compose.koinViewModel

/**
 * New "Session Details" screen (dark redesign) — read-only presentation of a logged session.
 * Reached from a row in [com.goskar.boardgame.ui.gamesHistory.newHistory.HistoryNewScreen].
 * Backed by [SessionDetailsNewViewModel] (placeholder state only).
 */
class SessionDetailsNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: SessionDetailsNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        SessionDetailsNewScreenContent(
            state = state,
            onBack = { navigator?.pop() },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionDetailsNewScreenContent(
    state: SessionDetailsNewState,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    Scaffold(
        containerColor = BoardGameDarkColors.Background,
        topBar = {
            BgDarkTopBar(
                title = "Session Details",
                onBack = onBack,
                trailingContent = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BoardGameDarkColors.Primary,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameDarkSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            BgDarkListCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(BoardGameShapes.Medium)
                            .background(BoardGameDarkColors.SurfaceContainerHigh),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            state.gameName,
                            style = BoardGameDarkTypography.HeadlineMd,
                            color = BoardGameDarkColors.OnSurface,
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BgDarkChip(state.category, state.categoryStyle)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                state.dateLabel,
                                style = BoardGameDarkTypography.BodyMd,
                                color = BoardGameDarkColors.OnSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Stat cells
            BgDarkListCard {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatCell("DATE", state.dateLabel, Modifier.weight(1f))
                    CellDivider()
                    StatCell("DURATION", state.duration, Modifier.weight(1f))
                    CellDivider()
                    StatCell("PLAYERS", state.playerCount, Modifier.weight(1f))
                }
            }

            // Players
            BgDarkSectionHeader(title = "Players")
            BgDarkListCard {
                state.players.forEachIndexed { index, player ->
                    PlayerResultRow(player)
                    if (index < state.players.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(
                            color = BoardGameDarkColors.Divider,
                            thickness = 0.5.dp,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Variants
            BgDarkSectionHeader(title = "Session Variants")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.variants.forEach { variant ->
                    BgDarkChip(variant, com.goskar.boardgame.ui.theme.BgDarkChipStyle.EXPANSION)
                }
            }

            // Notes
            BgDarkSectionHeader(title = "Notes")
            BgDarkListCard {
                Text(
                    state.notes,
                    style = BoardGameDarkTypography.BodyLg,
                    color = BoardGameDarkColors.OnSurfaceVariant,
                )
            }

            Spacer(Modifier.height(4.dp))
            BgDarkPrimaryButton(
                text = "Edit Session",
                onClick = onEdit,
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                },
            )
            BgDarkGhostButton(text = "Delete Session", onClick = onDelete)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = BoardGameDarkTypography.LabelMd,
            color = BoardGameDarkColors.OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = BoardGameDarkTypography.TitleLg,
            color = BoardGameDarkColors.OnSurface,
        )
    }
}

@Composable
private fun CellDivider() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(32.dp)
            .background(BoardGameDarkColors.Divider),
    )
}

@Composable
private fun PlayerResultRow(player: SessionPlayerResult) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BgDarkAvatar(initials = player.initials, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                player.name,
                style = BoardGameDarkTypography.BodyLg.copy(
                    fontWeight = if (player.isWinner) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = if (player.isWinner) BoardGameDarkColors.Success else BoardGameDarkColors.OnSurface,
            )
            if (player.isWinner) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = BoardGameDarkColors.Success,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Winner",
                        style = BoardGameDarkTypography.LabelMd,
                        color = BoardGameDarkColors.Success,
                    )
                }
            }
        }
        Text(
            player.score,
            style = BoardGameDarkTypography.TitleLg,
            color = if (player.isWinner) BoardGameDarkColors.Success else BoardGameDarkColors.OnSurface,
        )
    }
}

@Preview(name = "Session Details — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SessionDetailsNewScreenPreview() {
    SessionDetailsNewScreenContent(state = SessionDetailsNewState())
}
