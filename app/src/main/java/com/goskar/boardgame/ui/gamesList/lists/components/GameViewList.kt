package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gamesList.lists.GameListState
import com.goskar.boardgame.ui.gamesList.lists.GameUiState

@Composable
fun GameViewList(
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
    state: GameListState,
    update: (GameListState) -> Unit = {},
    refreshGameList: () -> Unit = {},
    updateExpandedGameCover: (Game) -> Unit = {},
    changeAllExpendedGameCover: () -> Unit = {}
) {
    var selectedList by remember { mutableStateOf(GameListFormatEnum.LIST) }

    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    var isScrollingDown by remember { mutableStateOf(false) }
    val lastOffset by remember { mutableIntStateOf(0) }

    Column {
        AnimatedVisibility(visible = !isScrollingDown) {
            Column {
                Row {
                    FormatCard(
                        Modifier.weight(1f),
                        "List",
                        isSelected = selectedList == GameListFormatEnum.LIST,
                        onClick = { selectedList = GameListFormatEnum.LIST })
                    FormatCard(
                        Modifier.weight(1f),
                        "Square",
                        isSelected = (selectedList == GameListFormatEnum.SQUARE_COVER || selectedList == GameListFormatEnum.SQUARE_INFO),
                        onClick = {
                            if (selectedList == GameListFormatEnum.SQUARE_COVER) changeAllExpendedGameCover()
                            else selectedList = GameListFormatEnum.SQUARE_COVER
                        })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    GameCheckbox(
                        modifier = Modifier.weight(1f),
                        checkboxText = stringResource(R.string.board_base),
                        checked = state.checkboxBaseGame,
                        onCheckedChange = {
                            update(
                                state.copy(
                                    checkboxBaseGame = !state.checkboxBaseGame,
                                )
                            )
                            refreshGameList()

                        }
                    )
                    GameCheckbox(
                        modifier = Modifier.weight(1f),
                        checkboxText = stringResource(R.string.board_expansion),
                        checked = state.checkboxExpansionGame,
                        onCheckedChange = {
                            update(
                                state.copy(
                                    checkboxExpansionGame = !state.checkboxExpansionGame,
                                )
                            )
                            refreshGameList()
                        }
                    )

                }
            }
        }

        if (selectedList == GameListFormatEnum.LIST) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(10.dp),
            ) {
                items(items = state.gameListEdited) { gameUi ->
                    isScrollingDown = listState.firstVisibleItemIndex != lastOffset

                    SingleLineGame(
                        game = gameUi.game,
                        modifier = Modifier.padding(
                            bottom = if (state.gameListEdited.indexOf(gameUi) == (state.gameListEdited.size - 1)
                            ) 60.dp else 10.dp
                        ),
                        deleteGame = deleteGame,
                        refresh = refresh,
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items = state.gameListEdited, key = { it.game.id }) { gameUi ->
                    isScrollingDown = gridState.firstVisibleItemIndex != lastOffset

                    val gameUri = gameUi.game.uriFromBgg ?: gameUi.game.uri
                    if (gameUi.isExpanded && !gameUri.isNullOrEmpty()) {
                        SingleCoverGameCard(
                            game = gameUi.game,
                            modifier = Modifier.padding(
                                bottom = if (state.gameListEdited.indexOf(gameUi) == (state.gameListEdited.size - 1)
                                ) 60.dp else 10.dp
                            ),
                            deleteGame = deleteGame,
                            refresh = refresh,
                            onCardCLick = { updateExpandedGameCover(gameUi.game) }
                        )
                    } else {
                        SingleGameCard(
                            game = gameUi.game,
                            modifier = Modifier.padding(
                                bottom = if (state.gameListEdited.indexOf(gameUi) == (state.gameListEdited.size - 1)
                                ) 60.dp else 10.dp
                            ),
                            deleteGame = deleteGame,
                            refresh = refresh,
                            onCardCLick = { updateExpandedGameCover(gameUi.game) }
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun GameViewListPreview() {
    val game = Game(
        name = "Marvel",
        expansion = false,
        cooperate = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh1"
    )
    val game2 = Game(
        name = "Scrable",
        expansion = true,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh2"
    )
    val gameList = listOf(game, game2)
    val gameUiStates = gameList.map { GameUiState(game = it) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        GameViewList(state = GameListState(gameUiStates))
    }
}