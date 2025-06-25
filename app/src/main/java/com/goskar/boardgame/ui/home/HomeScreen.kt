package com.goskar.boardgame.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.bottomBar.BottomBarElements
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gameRaports.GameReportsScreen
import com.goskar.boardgame.ui.gameSearchBGG.GameSearchScreen
import com.goskar.boardgame.ui.gamesHistory.HistoryGameListScreen
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.home.components.OtherBottomMenu
import com.goskar.boardgame.ui.home.components.OtherBottomMenuList
import com.goskar.boardgame.ui.login.LoginScreen
import com.goskar.boardgame.ui.playerList.PlayerListScreen
import com.goskar.boardgame.ui.theme.SmoochBold24LetterSpacing2
import org.koin.androidx.compose.koinViewModel

class HomeScreen(private val firstLogin: Boolean) : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val viewModel: HomeScreenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val itemsMenu by viewModel.otherItems.collectAsState()

        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()

        LaunchedEffect(firstLogin) {
            if (firstLogin) {
                viewModel.getAllData()
            }
        }
        LaunchedEffect(state.isSignOut) {
            if (state.isSignOut) {
                navigator?.replaceAll(LoginScreen())
                ScreenLifecycleStore.remove(this@HomeScreen)
            }
        }
        BoardGameScaffold(
            titlePage = stringResource(R.string.app_name),
            selectedScreen = BottomBarElements.HomeButton.title,
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase

        ) { paddingValues ->
            HomeScreenContent(state = state, paddingValues = paddingValues, itemsMenu = itemsMenu)
        }
    }
}

@Composable
fun HomeScreenContent(
    state: HomeScreenState,
    paddingValues: PaddingValues,
    itemsMenu: List<OtherBottomMenuList>
) {
    val navigator = LocalNavigator.current

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_user),
                    contentDescription = stringResource(id = R.string.player_list),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    stringResource(id = R.string.player_list),
                    style = SmoochBold24LetterSpacing2,
                )
            }
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_puzzle),
                    contentDescription = stringResource(id = R.string.board_list),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    stringResource(id = R.string.board_list),
                    style = SmoochBold24LetterSpacing2,
                )
            }

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_search),
                    contentDescription = stringResource(id = R.string.search_bgg),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    stringResource(id = R.string.search_bgg),
                    style = SmoochBold24LetterSpacing2,
                )
            }
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_clock),
                    contentDescription = stringResource(id = R.string.history_game_screen),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    stringResource(id = R.string.history_game_screen),
                    style = SmoochBold24LetterSpacing2,
                )
            }
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_chart),
                    contentDescription = stringResource(id = R.string.reports),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    stringResource(id = R.string.reports),
                    style = SmoochBold24LetterSpacing2,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            OtherBottomMenu(items = itemsMenu)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BoardGameScaffold(
        titlePage = stringResource(R.string.app_name),
        selectedScreen = BottomBarElements.HomeButton.title,
    ) { paddingValues ->
        HomeScreenContent(state = HomeScreenState(isLoading = false), paddingValues, emptyList())
    }
}