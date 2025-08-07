package com.goskar.boardgame.ui.gamesList.play.components

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gamesList.play.ExpansionGameUiState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.SmoochBold26

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectExpansionDialog(
    gameExpansionList: List<ExpansionGameUiState>,
    selectExpansion: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Column(
            modifier = Modifier
                .onPreInterceptKeyBeforeSoftKeyboard { event ->
                    if (event.key.nativeKeyCode == KeyEvent.KEYCODE_BACK) {
                        focusManager.clearFocus()
                        true
                    } else {
                        false
                    }
                }
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
                .padding(10.dp),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(10.dp),
            ) {
                items(items = gameExpansionList) { expansion ->
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .fillParentMaxWidth()
                            .padding(bottom = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                }
                        ) {
                            Checkbox(
                                checked = expansion.isSelected, onCheckedChange = {
                                    selectExpansion(expansion.game.id)
                                })
                            Text(
                                text = expansion.game.name,
                                style = SmoochBold26,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview()
@Composable
fun SelectExpansionDialogPreview() {

    val game = Game(
        name = "Marvel",
        expansion = false,
        cooperate = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfshdasdas"
    )
    val game2 = Game(
        name = "Ddatek 2",
        expansion = false,
        cooperate = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfshdasdas"
    )
    val gameList = listOf(game, game2)
    val gameUiStates = gameList.map { ExpansionGameUiState(game = it) }


    BoardGameTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(10.dp)) {

                SelectExpansionDialog(
                    gameExpansionList = gameUiStates
                )
            }
        }
    }
}