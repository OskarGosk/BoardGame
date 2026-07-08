package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AppOptionGrid(
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
        horizontalArrangement = Arrangement.spacedBy(0.dp),
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
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    option.icon, null,
                    tint = if (isSelected) cs.onPrimary else cs.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    option.label,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                    color = if (isSelected) cs.onPrimary else cs.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AppOptionGridPreviewContent() {
    var sel by remember { mutableStateOf(0) }
    val options = listOf(SkillOption("Beginner", Icons.Default.School), SkillOption("Master", Icons.Default.EmojiEvents))
    AppOptionGrid(options, sel, { sel = it }, Modifier.padding(16.dp))
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppOptionGridLight() = BoardGameTheme(darkTheme = false) { AppOptionGridPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppOptionGridDark() = BoardGameTheme(darkTheme = true) { AppOptionGridPreviewContent() }
