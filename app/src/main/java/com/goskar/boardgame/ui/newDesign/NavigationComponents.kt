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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BgNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

// =============================================================================
// NAVIGATION — Light
// =============================================================================

/**
 * Bottom navigation bar — frosted linen bg, active terracotta dot below icon.
 * Matches the 4-tab nav on all light screens: Home / Players / Games / History.
 */
@Composable
fun BgBottomNavBar(
    items: List<BgNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BoardGameColors.NavBackground,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val isActive = index == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = 28.dp)
                        ) { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (isActive) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = if (isActive) BoardGameColors.Primary else BoardGameColors.OnSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        style = BoardGameTypography.LabelCaps.copy(
                            letterSpacing = 0.sp,
                            fontSize = 11.sp
                        ),
                        color = if (isActive) BoardGameColors.Primary else BoardGameColors.OnSurfaceVariant,
                    )
                    Spacer(Modifier.height(3.dp))
                    // Active dot
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(if (isActive) BoardGameColors.NavActiveDot else Color.Transparent)
                    )
                }
            }
        }
    }
}

/**
 * Top app bar — optional leading slot (e.g. avatar) OR back arrow + title + optional trailing icon.
 * Matches every light screen header. When [leading] is provided it replaces the back arrow.
 */
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
            .background(BoardGameColors.Background)
            .padding(horizontal = BoardGameSpacing.MarginMobile),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        } else if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = BoardGameColors.Primary)
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(
            title, style = BoardGameTypography.HeadlineMd, color = BoardGameColors.Primary,
            modifier = Modifier.weight(1f)
        )
        trailingIcon?.invoke()
    }
}

// =============================================================================
// NAVIGATION — Dark
// =============================================================================

/**
 * Dark bottom nav — active tab gets an orange pill background.
 * Matches the 4-tab nav on all dark screens: Home / Collection / Add Session / Players.
 */
@Composable
fun BgDarkBottomNavBar(
    items: List<BgNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BoardGameDarkColors.NavBackground,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column {
            HorizontalDivider(color = BoardGameDarkColors.CardBorder, thickness = 0.5.dp)
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
                                    .background(BoardGameDarkColors.NavActiveChip)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 28.dp)
                                ) { onSelect(index) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = if (isActive) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = if (isActive) BoardGameDarkColors.NavActiveTint else BoardGameDarkColors.OnSurfaceVariant,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = item.label,
                                style = BoardGameDarkTypography.LabelMd,
                                color = if (isActive) BoardGameDarkColors.NavActiveTint else BoardGameDarkColors.OnSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dark top app bar — hamburger OR back + title + trailing.
 */
@Composable
fun BgDarkTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showMenu: Boolean = false,
    onMenuClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(BoardGameDarkColors.Background)
            .padding(horizontal = BoardGameDarkSpacing.MarginMobile),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = BoardGameDarkColors.Primary)
            }
            Spacer(Modifier.width(4.dp))
        } else if (showMenu) {
            IconButton(onClick = onMenuClick ?: {}) {
                Icon(Icons.Default.Menu, "Menu", tint = BoardGameDarkColors.OnSurfaceVariant)
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(
            title, style = BoardGameDarkTypography.TitleLg, color = BoardGameDarkColors.Primary,
            modifier = Modifier.weight(1f)
        )
        trailingContent?.invoke()
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

private val lightNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Players", Icons.Default.Person),
    BgNavItem("Games", Icons.AutoMirrored.Filled.List),
    BgNavItem("History", Icons.Default.DateRange),
)

private val darkNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
    BgNavItem("Add Session", Icons.Default.DateRange),
    BgNavItem("Players", Icons.Default.Person),
)

@Preview(name = "Navigation — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun NavigationLightPreview() {
    BoardGameTheme {
        Column {
            BgTopBar("Add Game", onBack = {}, trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Home,
                        null,
                        tint = BoardGameColors.OnSurfaceVariant
                    )
                }
            })
            Spacer(Modifier.weight(1f))
            var selected by remember { mutableStateOf(2) }
            BgBottomNavBar(lightNavItems, selected, { selected = it })
        }
    }
}

@Preview(name = "Navigation — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun NavigationDarkPreview() {
    BoardGameDarkTheme {
        Column {
            BgDarkTopBar("Tabletop Tracker", showMenu = true)
            Spacer(Modifier.weight(1f))
            var selected by remember { mutableStateOf(0) }
            BgDarkBottomNavBar(darkNavItems, selected, { selected = it })
        }
    }
}
