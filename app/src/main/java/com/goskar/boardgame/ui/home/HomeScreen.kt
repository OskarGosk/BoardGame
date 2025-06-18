package com.goskar.boardgame.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.gamesHistory.HistoryGameListScreen
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.playerList.PlayerListScreen
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.firebaseData.DataFromFirebase
import com.goskar.boardgame.ui.gameRaports.GameReportsScreen
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.theme.SmoochBold24LetterSpacing2
import org.koin.androidx.compose.koinViewModel

class HomeScreen(val firstLogin: Boolean) : Screen {
    @Composable
    override fun Content() {

        val viewModel: HomeScreenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(firstLogin) {
            if (firstLogin) {
                viewModel.getAllData()
            }
        }

        HomeScreenContent(state = state)
    }
}

@Composable
fun HomeScreenContent(
    state: HomeScreenState
) {
    val navigator = LocalNavigator.current

    BoardGameScaffold(
        titlePage = stringResource(R.string.app_name),
        selectedScreen = BottomBarElements.HomeButton.title

    ) { paddingValues ->
        if (state.isLoading) AppLoader(dimAmount = 0.8f)
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

            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(GameReportsScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
            ) {
                Text(
                    text = stringResource(R.string.reports),
                    style = SmoochBold24LetterSpacing2,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    navigator?.push(DataFromFirebase())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(48.dp),
            ) {
                Text(
                    text = "Inne",
                    style = SmoochBold24LetterSpacing2,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(state = HomeScreenState(isLoading = false))
}