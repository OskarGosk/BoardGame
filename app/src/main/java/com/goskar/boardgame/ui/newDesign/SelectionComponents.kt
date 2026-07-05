package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SkillOption(
    val label: String,
    val icon: ImageVector,
)


@Composable
fun BgSegmentedControl(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(BoardGameShapes.Full)
            .background(cs.surfaceContainerLow)
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
                        .background(if (isSelected) cs.surfaceContainerLowest else Color.Transparent)
                        .then(if (isSelected) Modifier.border(1.dp, cs.outlineVariant, BoardGameShapes.Full) else Modifier)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
                        color = if (isSelected) cs.primary else cs.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun BgSkillSelector(
    options: List<SkillOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(BoardGameShapes.Medium)
            .background(cs.surfaceContainerLow),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(BoardGameShapes.Medium)
                    .background(if (isSelected) cs.primary else Color.Transparent)
                    .clickable { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    option.icon, null,
                    tint = if (isSelected) cs.onPrimary else cs.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    option.label,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                    color = if (isSelected) cs.onPrimary else cs.onSurfaceVariant
                )
            }
        }
    }
}


private val previewSkillOptions = listOf(
    SkillOption("Beginner", Icons.Default.School),
    SkillOption("Intermediate", Icons.Default.Psychology),
    SkillOption("Master", Icons.Default.EmojiEvents),
)

@Preview(name = "Selection — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SelectionLightPreview() {
    BoardGameTheme(darkTheme = false) { SelectionPreviewContent() }
}

@Preview(name = "Selection — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SelectionDarkPreview() {
    BoardGameTheme(darkTheme = true) { SelectionPreviewContent() }
}

@Composable
private fun SelectionPreviewContent() {
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
