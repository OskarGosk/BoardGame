package com.goskar.boardgame.ui.gameSearchBGG

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.SearchRowGlobal
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import org.koin.androidx.compose.koinViewModel

class GameSearchScreen:Screen {
    @Composable
    override fun Content() {

        val viewModel:  GameSearchViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        GameSearchContent(
            state = state,
            update = viewModel::update
        )
    }
}

@Composable
fun GameSearchContent(
    state: GameSearchState,
    update: (GameSearchState) -> Unit = {}
) {
    BoardGameScaffold(
        titlePage = R.string.search_bgg,
        selectedScreen = BottomBarElements.HomeButton.title

    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(paddingValues)
        ){
            SearchRowGlobal(
                searchHelp = R.string.board_name,
                searchTxt = state.searchTxt,
                sortOption = state.sortOption,
                updateTxt = {
                    update(
                        state.copy(
                            searchTxt = it
                        )
                    )
                },
                clearTxt = {
                    update(
                        state.copy(
                            searchTxt = ""
                        )
                    )
                },
                updateSort = {
                    update(
                        state.copy(
                            sortOption = it
                        )
                    )
                }
            )
        }
        val uriHandler = LocalUriHandler.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource( R.drawable.bgg),
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

@Preview
@Composable
fun GameSearchContentPreview(){
    GameSearchContent(
        state = GameSearchState()
    )
}