package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =============================================================================
// CHIPS — Light
// =============================================================================

enum class BgChipStyle { CATEGORY, STATUS_WIN, STATUS_PLACE, YEAR, EXPANSION, BASE_GAME }

/**
 * Status / category chip — small pill, semi-transparent bg.
 * Matches "STRATEGY", "2018", "WON", "Base Game", "Expansion".
 */
@Composable
fun BgChip(
    text: String,
    style: BgChipStyle = BgChipStyle.CATEGORY,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val (bg, fg) = when (style) {
        BgChipStyle.CATEGORY -> BoardGameColors.SecondaryContainer to BoardGameColors.OnSecondaryContainer
        BgChipStyle.STATUS_WIN -> BoardGameColors.SuccessBg to BoardGameColors.Success
        BgChipStyle.STATUS_PLACE -> BoardGameColors.SurfaceContainerHigh to BoardGameColors.OnSurfaceVariant
        BgChipStyle.YEAR -> BoardGameColors.SurfaceContainerHigh to BoardGameColors.OnSurfaceVariant
        BgChipStyle.EXPANSION -> BoardGameColors.PrimaryTint10 to BoardGameColors.Primary
        BgChipStyle.BASE_GAME -> BoardGameColors.SecondaryContainer to BoardGameColors.Secondary
    }
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(bg)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = BoardGameTypography.LabelCaps, color = fg)
    }
}

/**
 * Filter chip — toggleable, used in "All / Base Games / Expansions" bar.
 */
@Composable
fun BgFilterChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) BoardGameColors.SurfaceContainerLowest else BoardGameColors.SurfaceContainerLow)
            .border(
                1.dp,
                if (selected) BoardGameColors.Primary else Color.Transparent,
                BoardGameShapes.Full
            )
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BoardGameTypography.BodySm.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
            color = if (selected) BoardGameColors.Primary else BoardGameColors.OnSurfaceVariant,
        )
    }
}

/**
 * Variant chip — toggleable pill for session variants ("Epic Mode", "House Rules").
 */
@Composable
fun BgVariantChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) BoardGameColors.Primary else BoardGameColors.SurfaceContainerLow)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BoardGameTypography.BodySm,
            color = if (selected) BoardGameColors.OnPrimary else BoardGameColors.OnSurfaceVariant,
        )
    }
}

// =============================================================================
// CHIPS — Dark
// =============================================================================

enum class BgDarkChipStyle {
    CATEGORY, STATUS_WIN, STATUS_PLACE, TRENDING, EXPANSION, BASE_GAME, TAG_INACTIVE
}

/**
 * Dark status / category chip — small, semi-transparent bg.
 * Matches "WINNER", "Trending #1", "Base Game", "Expansion" on dark screens.
 */
@Composable
fun BgDarkChip(
    text: String,
    style: BgDarkChipStyle = BgDarkChipStyle.CATEGORY,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val (bg, fg, stroke) = when (style) {
        BgDarkChipStyle.CATEGORY -> Triple(
            BoardGameDarkColors.SurfaceContainerHigh,
            BoardGameDarkColors.OnSurfaceVariant,
            Color.Transparent
        )

        BgDarkChipStyle.STATUS_WIN -> Triple(
            BoardGameDarkColors.SuccessBg,
            BoardGameDarkColors.Success,
            BoardGameDarkColors.Success.copy(alpha = 0.4f)
        )

        BgDarkChipStyle.STATUS_PLACE -> Triple(
            BoardGameDarkColors.SurfaceContainerHigh,
            BoardGameDarkColors.OnSurfaceVariant,
            Color.Transparent
        )

        BgDarkChipStyle.TRENDING -> Triple(
            BoardGameDarkColors.SuccessBg,
            BoardGameDarkColors.Success,
            BoardGameDarkColors.Success.copy(alpha = 0.5f)
        )

        BgDarkChipStyle.EXPANSION -> Triple(
            BoardGameDarkColors.PrimaryTint10,
            BoardGameDarkColors.Primary,
            BoardGameDarkColors.OutlineVariant
        )

        BgDarkChipStyle.BASE_GAME -> Triple(
            BoardGameDarkColors.SecondaryContainer,
            BoardGameDarkColors.OnSecondaryContainer,
            Color.Transparent
        )

        BgDarkChipStyle.TAG_INACTIVE -> Triple(
            BoardGameDarkColors.SurfaceContainerHigh,
            BoardGameDarkColors.OnSurfaceVariant,
            BoardGameDarkColors.OutlineVariant
        )
    }
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Small)
            .background(bg)
            .then(
                if (stroke != Color.Transparent) Modifier.border(
                    1.dp,
                    stroke,
                    BoardGameShapes.Small
                ) else Modifier
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = BoardGameDarkTypography.LabelLg, color = fg)
    }
}

/**
 * Dark filter chip — orange fill when active.
 */
@Composable
fun BgDarkFilterChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) BoardGameDarkColors.OrangeFill else BoardGameDarkColors.SurfaceContainerHigh)
            .border(
                1.dp,
                if (selected) Color.Transparent else BoardGameDarkColors.CardBorder,
                BoardGameShapes.Full
            )
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BoardGameDarkTypography.BodyMd.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
            color = if (selected) BoardGameDarkColors.OnPrimary else BoardGameDarkColors.OnSurfaceVariant,
        )
    }
}

/**
 * Dark variant chip — "Rise of Ix Expansion", "Epic Mode" etc.
 */
@Composable
fun BgDarkVariantChip(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(BoardGameShapes.Full)
            .background(if (selected) BoardGameDarkColors.OrangeFill else BoardGameDarkColors.SurfaceContainer)
            .border(
                1.dp,
                if (selected) Color.Transparent else BoardGameDarkColors.OutlineVariant,
                BoardGameShapes.Full
            )
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BoardGameDarkTypography.BodyMd,
            color = if (selected) BoardGameDarkColors.OnPrimary else BoardGameDarkColors.OnSurfaceVariant,
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Chips — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ChipsLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BgChip("STRATEGY", BgChipStyle.CATEGORY)
                BgChip("2018", BgChipStyle.YEAR)
                BgChip("WON", BgChipStyle.STATUS_WIN)
                BgChip("Base Game", BgChipStyle.BASE_GAME)
                BgChip("Expansion", BgChipStyle.EXPANSION)
            }
            var filter by remember { mutableStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Base Games", "Expansions").forEachIndexed { i, l ->
                    BgFilterChip(l, filter == i, { filter = i })
                }
            }
            var v by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BgVariantChip("Rise of Ix", v, { v = !v })
                BgVariantChip("Epic Mode", !v, { v = !v })
            }
        }
    }
}

@Preview(name = "Chips — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ChipsDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BgDarkChip("WINNER", BgDarkChipStyle.STATUS_WIN)
                BgDarkChip("Trending #1", BgDarkChipStyle.TRENDING)
                BgDarkChip("Base Game", BgDarkChipStyle.BASE_GAME)
                BgDarkChip("Expansion", BgDarkChipStyle.EXPANSION)
            }
            var filter by remember { mutableStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Base Games", "Expansions").forEachIndexed { i, l ->
                    BgDarkFilterChip(l, filter == i, { filter = i })
                }
            }
            var v by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BgDarkVariantChip("Rise of Ix", v, { v = !v })
                BgDarkVariantChip("Epic Mode", !v, { v = !v })
            }
        }
    }
}
