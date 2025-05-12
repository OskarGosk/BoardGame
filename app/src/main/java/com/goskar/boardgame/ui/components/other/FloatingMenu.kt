package com.goskar.boardgame.ui.components.other

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.utils.FloatingMenuList

@Composable
fun FloatingMenu(
    items: List<FloatingMenuList>?
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (isExpanded && !items.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items.size) {
                    FloatingMenuItem(
                        icon = items[it].icon,
                        name = items[it].name,
                        screen = items[it].screen
                    )
                }
            }
        }


        val transition = updateTransition(targetState = isExpanded, label = "floatingButton")
        val rotation by transition.animateFloat(label = "rotation") {
            if (it) 315f else 0f
        }
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .padding(top = if (isExpanded) 10.dp else 0.dp)
                .size(50.dp)
                .rotate(rotation),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.board_add)
            )
        }

    }
}

@Composable
fun FloatingMenuItem(icon: ImageVector?, name: Int?, screen: Screen) {
    val navigator = LocalNavigator.current
    Row(
        modifier = Modifier
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (name != null) {
            FloatingActionButton(
                onClick = { navigator?.push(screen) },
                modifier = Modifier
                    .height(40.dp)
            ) {
                Text(text = stringResource(name),
                    style = SmoochBold18,)
            }
        }
        if (icon != null) {
            FloatingActionButton(
                onClick = { navigator?.push(screen) },
                modifier = Modifier
                    .padding(start = if (name != null) 5.dp else 0.dp)
                    .size(40.dp)

            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FloatingMenuPreview() {
    val items = listOf(
        FloatingMenuList(Icons.Default.Search, null, HomeScreen()),
        FloatingMenuList(Icons.Default.Delete, R.string.delete, HomeScreen())
    )

    FloatingMenu(items)
}