package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
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
import com.goskar.boardgame.ui.navigation.BgNavItem
import com.goskar.boardgame.ui.navigation.previewNavItems

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: String,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    navItems: List<BgNavItem>? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = title, onBack = onBack, trailing = trailing) },
        bottomBar = {
            if (navItems != null) {
                AppBottomNavBar(items = navItems, selected = selectedTab, onSelect = onTabSelected)
            }
        },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}

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
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(name = "AppScaffold — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppScaffoldDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        var selected by remember { mutableStateOf(0) }
        AppScaffold(
            title = "Tabletop Tracker",
            navItems = previewNavItems,
            selectedTab = selected,
            onTabSelected = { selected = it },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
