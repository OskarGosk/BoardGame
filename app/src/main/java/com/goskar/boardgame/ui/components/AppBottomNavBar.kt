package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.navigation.BgNavItem
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.ui.navigation.previewNavItems
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    items: List<BgNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    windowInsets: WindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
) {
    val navigator = LocalNavigator.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.windowInsetsPadding(windowInsets)) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    val isActive = index == selected
                    val activeColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .fillMaxHeight()
                                    .clip(BoardGameShapes.Full)
                                    .background(activeColor.copy(alpha = 0.15f)),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 28.dp),
                                ) {
                                    onSelect(index)
                                    navigator?.push(item.navigationScreen)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = if (isActive) item.selectedIcon else item.icon,
                                contentDescription = stringResource(item.labelRes),
                                tint = if (isActive) activeColor else inactiveColor,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(item.labelRes),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isActive) activeColor else inactiveColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomNavBarPreviewContent() {
    var sel by remember { mutableStateOf(0) }
    AppBottomNavBar(items = previewNavItems, selected = sel, onSelect = { sel = it })
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppBottomNavBarLight() =
    BoardGameTheme(darkTheme = false) { AppBottomNavBarPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppBottomNavBarDark() =
    BoardGameTheme(darkTheme = true) { AppBottomNavBarPreviewContent() }
