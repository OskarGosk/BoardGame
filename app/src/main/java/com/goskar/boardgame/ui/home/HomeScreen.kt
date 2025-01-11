package com.goskar.boardgame.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.games.lists.GameListScreen
import com.goskar.boardgame.ui.player.PlayerListScreen
import pl.ecp.app.ui.components.scaffold.BoardGameScaffold

class HomeScreen :Screen {
    @Composable
    override fun Content() {
        HomeScreenContent()
    }
}

@Composable
fun HomeScreenContent(

) {

    val navigator = LocalNavigator.current

    BoardGameScaffold(
        titlePage = stringResource( id = R.string.app_name)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding( 10.dp)
                .padding(paddingValues)
        ) {
            FloatingActionButton(
                onClick = {
                    navigator?.push(PlayerListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.player_list))
            }
            Spacer(modifier = Modifier.height(15.dp))
            FloatingActionButton(
                onClick = {
                    navigator?.push(GameListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.board_list))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
    )
}