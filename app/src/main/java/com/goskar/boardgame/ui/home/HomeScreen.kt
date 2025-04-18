package com.goskar.boardgame.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gamesHistory.HistoryGameListScreen
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.playerList.PlayerListScreen
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.theme.SmoochBold24LetterSpacing2
import org.koin.androidx.compose.koinViewModel

class HomeScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel: HomeScreenViewModel = koinViewModel()

        HomeScreenContent()
    }
}

@Composable
fun HomeScreenContent() {
    val navigator = LocalNavigator.current

    BoardGameScaffold(
        titlePage = R.string.app_name,
        selectedScreen = BottomBarElements.HomeButton.title

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(paddingValues)
        ) {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(PlayerListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .height(48.dp),
            ) {
                Text(
                    stringResource(id = R.string.player_list),
                    style = SmoochBold24LetterSpacing2,
                )
            }
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(GameListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
            ) {
                Text(
                    stringResource(id = R.string.board_list),
                    style = SmoochBold24LetterSpacing2,
                )
            }
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(HistoryGameListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
            ) {
                Text(
                    stringResource(id = R.string.history_game_screen),
                    style = SmoochBold24LetterSpacing2,
                )
            }

            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(GameSearchScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
            ) {
                Text(
                    text = stringResource(R.string.search_bgg),
                    style = SmoochBold24LetterSpacing2,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent()
}