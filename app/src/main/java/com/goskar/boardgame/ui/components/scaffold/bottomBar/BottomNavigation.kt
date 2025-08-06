package com.goskar.boardgame.ui.components.scaffold.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.primaryLight


@Composable
fun BottomNavigation(
    selectedScreen: Int?
) {
    val navigator = LocalNavigator.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(MaterialTheme.colorScheme.background), // Jeśli chcesz kolor
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomBarElements.entries.forEach { elements ->
            Column(
                modifier = Modifier
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(72.dp)
                        .padding(bottom = 4.dp),
                    color = if (selectedScreen == elements.title) primaryLight else White
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.width(72.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                elements.navigationScreen?.let {
                                    navigator?.replace(it)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(elements.title),
                            textAlign = TextAlign.Center,
                            style = if (selectedScreen == elements.title) SmoochBold18.copy(color = primaryLight) else Smooch18,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BottomBarNavigationPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        BottomNavigation(R.string.home)
    }
}
