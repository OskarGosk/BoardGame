package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.navigation.BgNavItem
import com.goskar.boardgame.ui.navigation.previewNavItems


@Composable
fun BgBottomNavBar(
    items: List<BgNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
        tonalElevation = 0.dp,
    ) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
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
                                    .background(activeColor.copy(alpha = 0.15f))
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 28.dp)
                                ) {
                                    onSelect(index)
                                    navigator?.push(item.navigationScreen)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = if (isActive) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = if (isActive) activeColor else inactiveColor,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = item.label,
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
fun BgTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = BoardGameSpacing.MarginMobile),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        } else if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        trailingIcon?.invoke()
    }
}


@Preview(name = "Navigation — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun NavigationLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Column {
            BgTopBar("Add Game", onBack = {}, trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Home, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            })
            Spacer(Modifier.weight(1f))
            var selected by remember { mutableStateOf(2) }
            BgBottomNavBar(previewNavItems, selected, { selected = it })
        }
    }
}

@Preview(name = "Navigation — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun NavigationDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Column {
            BgTopBar("Tabletop Tracker")
            Spacer(Modifier.weight(1f))
            var selected by remember { mutableStateOf(0) }
            BgBottomNavBar(previewNavItems, selected, { selected = it })
        }
    }
}
