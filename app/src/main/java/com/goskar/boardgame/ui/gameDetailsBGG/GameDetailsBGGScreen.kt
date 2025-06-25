package com.goskar.boardgame.ui.gameDetailsBGG

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.BoardGameBGG
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gameDetailsBGG.components.AddGameDialog
import com.goskar.boardgame.ui.gamesList.lists.components.GameDataRow
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold24LetterSpacing2
import org.koin.androidx.compose.koinViewModel

class GameDetailsBGGScreen(val gameID: String, val gameName: String) : Screen {

    @Composable
    override fun Content() {
        val viewModel: GameDetailsBGGViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val allBaseGame by viewModel.allBaseGame.collectAsState()
        val navigator = LocalNavigator.current

        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()

        LaunchedEffect(state.successAddEditGame) {
            if (state.successAddEditGame) {
                navigator?.pop()
            }
        }

        LaunchedEffect(gameID) {
            viewModel.update(
                state.copy(
                    gameName = gameName,
                    gameId = gameID
                )
            )
            viewModel.getGame()
        }
        val gameDetails by viewModel.gameDetails.collectAsState()

        BoardGameScaffold(
            titlePage = stringResource(R.string.details_bgg),
            selectedScreen = null,
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase
        ) { paddingValues ->
            GameDetailsBGGContent(
                state = state,
                allBaseGame = allBaseGame,
                gameDetails = gameDetails?.boardGamesBGG?.firstOrNull(),
                addGame = viewModel::validateAddGame,
                update = viewModel::update,
                errorReload = viewModel::getGame,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun GameDetailsBGGContent(
    state: GameDetailsBGGState,
    allBaseGame: List<Game>,
    gameDetails: BoardGameBGG?,
    addGame: () -> Unit = {},
    update: (GameDetailsBGGState) -> Unit = {},
    errorReload: () -> Unit = {},
    paddingValues: PaddingValues
) {

    var showAddEditDialog by remember { mutableStateOf(false) }
    var expendedDesc by remember { mutableStateOf(false) }

    if (state.isLoading) AppLoader()
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 10.dp)
            .padding(bottom = 40.dp)
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gameDetails?.image)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(1f)
                    .size(150.dp)
                    .padding(10.dp)
            )

            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = state.gameName ?: "", style = SmoochBold18)
                GameDataRow(R.string.board_min_player, gameDetails?.minPlayers.toString())
                GameDataRow(R.string.board_max_player, gameDetails?.maxPlayers.toString())
                GameDataRow(R.string.bgg_year, gameDetails?.yearPublished.toString())
                GameDataRow(R.string.bgg_age, gameDetails?.age.toString())
                GameDataRow(R.string.bgg_playing_time, gameDetails?.playingTime.toString())
            }
        }

        Text(text = stringResource(R.string.history_description), style = Smooch14)

        Text(
            text = gameDetails?.description ?: "No description on BGG server",
            style = Smooch16,
            overflow = TextOverflow.Ellipsis,
            maxLines = if (expendedDesc) 100 else 2,
            modifier = Modifier
                .clickable { expendedDesc = !expendedDesc })
    }

    if (showAddEditDialog) {
        AddGameDialog(
            state = state,
            allBaseGame = allBaseGame,
            confirmButtonClick = {
                showAddEditDialog = false
                addGame()
            },
            onDismiss = { showAddEditDialog = !showAddEditDialog },
            update = update
        )
    }

    val uriHandler = LocalUriHandler.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    if (state.isError) errorReload() else showAddEditDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(horizontal = 10.dp)
                    .height(48.dp),
            ) {
                Text(
                    text = stringResource(if (state.isError) R.string.bgg_error_game_details else R.string.bgg_add_game),
                    style = SmoochBold24LetterSpacing2,
                )
            }

            Image(
                painter = painterResource(R.drawable.bgg),
                contentScale = ContentScale.Fit,
                contentDescription = "BGG LOGO",
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable {
                        uriHandler.openUri("https://boardgamegeek.com/")
                    })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameDetailsBGGPreview() {
    val game = BoardGameBGG(
        minPlayers = 1,
        maxPlayers = 2,
        yearPublished = 2023,
        age = 12,
        playingTime = 40,
        description = "Tutaj długi descriptionm ponad dwqie linijki musi mieć, aby zobaczyć jak wygląda bez rozwinięcia. Chodzi przedewszystkim o widok trzech kropek, aby użytkownik wiedział, że można rozwinąć"
    )

    BoardGameScaffold(
        titlePage = stringResource(R.string.details_bgg),
        selectedScreen = null,
    ) { paddingValues ->

        GameDetailsBGGContent(
            state = GameDetailsBGGState(
                gameName = "Marvel"
            ),
            allBaseGame = emptyList(),
            gameDetails = game,
            paddingValues = paddingValues
        )
    }
}