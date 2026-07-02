package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: String,
    navItems: List<BgNavItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme(),
    onBack: (() -> Unit)? = null,
    showMenu: Boolean = false,
    onMenuClick: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = if (darkTheme) BoardGameDarkColors.Background else BoardGameColors.Background,
        topBar = {
            if (darkTheme) {
                BgDarkTopBar(
                    title = title,
                    onBack = onBack,
                    showMenu = showMenu,
                    onMenuClick = onMenuClick,
                    trailingContent = trailing,
                )
            } else {
                BgTopBar(
                    title = title,
                    onBack = onBack,
                    leading = leading,
                    trailingIcon = trailing,
                )
            }
        },
        bottomBar = {
            if (darkTheme) {
                BgDarkBottomNavBar(items = navItems, selected = selectedTab, onSelect = onTabSelected)
            } else {
                BgBottomNavBar(items = navItems, selected = selectedTab, onSelect = onTabSelected)
            }
        },
        content = content,
    )
}
private val previewNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Players", Icons.Default.Person),
    BgNavItem("Games", Icons.AutoMirrored.Filled.List),
    BgNavItem("History", Icons.Default.DateRange),
)

@Preview(name = "AppScaffold — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppScaffoldLightPreview() {
    BoardGameTheme(darkTheme = false) {
        var selected by remember { mutableStateOf(0) }
        AppScaffold(
            title = "Board Games",
            navItems = previewNavItems,
            selectedTab = selected,
            onTabSelected = { selected = it },
            darkTheme = false,
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content", style = BoardGameTypography.HeadlineMd, color = BoardGameColors.Primary)
            }
        }
    }
}

@Preview(name = "AppScaffold — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppScaffoldDarkPreview() {
    BoardGameDarkTheme {
        var selected by remember { mutableStateOf(0) }
        AppScaffold(
            title = "Tabletop Tracker",
            navItems = previewNavItems,
            selectedTab = selected,
            onTabSelected = { selected = it },
            darkTheme = true,
            showMenu = true,
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content", style = BoardGameDarkTypography.TitleLg, color = BoardGameDarkColors.Primary)
            }
        }
    }
}
