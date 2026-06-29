package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SkillOption(
    val label: String,
    val icon: ImageVector,
)

// =============================================================================
// SELECTION — Light
// =============================================================================

/**
 * Segmented control — sliding pill, two options.
 * Matches "BGG SEARCH / MANUAL ENTRY" and "Competitive / Cooperative".
 */
@Composable
fun BgSegmentedControl(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(BoardGameShapes.Full)
            .background(BoardGameColors.SurfaceContainerLow)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(BoardGameShapes.Full)
                        .background(if (isSelected) BoardGameColors.SurfaceContainerLowest else Color.Transparent)
                        .then(
                            if (isSelected) Modifier.border(
                                1.dp,
                                BoardGameColors.OutlineVariant,
                                BoardGameShapes.Full
                            ) else Modifier
                        )
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = BoardGameTypography.BodySm.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
                        color = if (isSelected) BoardGameColors.Primary else BoardGameColors.OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

/**
 * Skill level selector — icon + label tiles in a row.
 * Matches "Beginner / Intermediate / Master" on Add Player (light).
 */
@Composable
fun BgSkillSelector(
    options: List<SkillOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(BoardGameShapes.Medium)
            .background(BoardGameColors.SurfaceContainerLow),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(BoardGameShapes.Medium)
                    .background(if (isSelected) BoardGameColors.Primary else Color.Transparent)
                    .clickable { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    option.icon, null,
                    tint = if (isSelected) BoardGameColors.OnPrimary else BoardGameColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    option.label,
                    style = BoardGameTypography.LabelCaps.copy(letterSpacing = 0.sp),
                    color = if (isSelected) BoardGameColors.OnPrimary else BoardGameColors.OnSurfaceVariant
                )
            }
        }
    }
}

// =============================================================================
// SELECTION — Dark
// =============================================================================

/**
 * Dark segmented control — dark fill, orange active tab.
 * Matches "BGG Search / Manual Entry" on dark Add Game screen.
 */
@Composable
fun BgDarkSegmentedControl(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(BoardGameShapes.Medium)
            .background(BoardGameDarkColors.SurfaceContainerLow)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(BoardGameShapes.Medium)
                    .background(if (isSelected) BoardGameDarkColors.SecondaryContainer else Color.Transparent)
                    .clickable { onSelect(index) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = option,
                    style = BoardGameDarkTypography.BodyMd.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
                    color = if (isSelected) BoardGameDarkColors.Primary else BoardGameDarkColors.OnSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Dark option grid — icon + label square tiles.
 * Matches "Beginner / Intermediate / Master" on dark Add Player screen.
 */
@Composable
fun BgDarkOptionGrid(
    options: List<SkillOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(BoardGameShapes.Medium)
                    .background(if (isSelected) BoardGameDarkColors.SecondaryContainer else BoardGameDarkColors.SurfaceContainerHigh)
                    .then(
                        if (isSelected) Modifier.border(
                            1.5.dp,
                            BoardGameDarkColors.Primary,
                            BoardGameShapes.Medium
                        ) else Modifier
                    )
                    .clickable { onSelect(index) }
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    option.icon, null,
                    tint = if (isSelected) BoardGameDarkColors.Primary else BoardGameDarkColors.OnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    option.label,
                    style = BoardGameDarkTypography.LabelLg.copy(letterSpacing = 0.sp),
                    color = if (isSelected) BoardGameDarkColors.Primary else BoardGameDarkColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

private val previewSkillOptions = listOf(
    SkillOption("Beginner", Icons.Default.School),
    SkillOption("Intermediate", Icons.Default.Psychology),
    SkillOption("Master", Icons.Default.EmojiEvents),
)

@Preview(name = "Selection — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SelectionLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var tab by remember { mutableStateOf(0) }
            BgSegmentedControl(listOf("BGG Search", "Manual Entry"), tab, { tab = it })
            var skill by remember { mutableStateOf(0) }
            BgSkillSelector(previewSkillOptions, skill, { skill = it })
        }
    }
}

@Preview(name = "Selection — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SelectionDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var tab by remember { mutableStateOf(0) }
            BgDarkSegmentedControl(listOf("BGG Search", "Manual Entry"), tab, { tab = it })
            var skill by remember { mutableStateOf(0) }
            BgDarkOptionGrid(previewSkillOptions, skill, { skill = it })
        }
    }
}
