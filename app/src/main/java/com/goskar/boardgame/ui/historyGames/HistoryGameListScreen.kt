package com.goskar.boardgame.ui.historyGames

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.historyGames.lists.components.GamesHistorySearchRow
import com.goskar.boardgame.ui.historyGames.lists.components.HistoryGamesList

class HistoryGameListScreen : Screen {
    @Composable
    override fun Content() {
        HistoryGameListContent()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HistoryGameListContent() {
    BoardGameScaffold(
        titlePage = stringResource(id = R.string.history_game_screen)
    ) { paddingValues ->
        val focusManager = LocalFocusManager.current

        val history1 = HistoryGame(
            gameName = "Marvel",
            winner = "Oskar",
            gameData = "2025-01-23",
            listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
            description = "",
            id = "dsa"
        )

        val history2 = HistoryGame(
            gameName = "Scrable",
            winner = "Kamila",
            gameData = "2025-01-01",
            listOfPlayer = listOf("Oskar", "Kamila", "Gerard"),
            description = "",
            id = "dsa"
        )

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            GamesHistorySearchRow()
            HistoryGamesList(list = listOf(history1, history2, history1, history1,history2, history1, history2, history1, history1,history2, history1, history2, history1, history1,history2, history1, history2, history1, history1,history2, history1, history2, history1, history1,history2))
        }
    }
}

@Preview
@Composable
fun HistoryGameListContentPreview() {
    Surface(
    ) {
        HistoryGameListContent()
    }
}