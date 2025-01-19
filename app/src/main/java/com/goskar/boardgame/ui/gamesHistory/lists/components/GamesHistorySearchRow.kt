package com.goskar.boardgame.ui.gamesHistory.lists.components

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamesHistorySearchRow(
    onCLickMenu: () -> Unit = {},
//    update: (PlayerListState) -> Unit = {},
//    state: PlayerListState
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .onPreInterceptKeyBeforeSoftKeyboard { event ->
                if (event.key.nativeKeyCode == KeyEvent.KEYCODE_BACK) {
                    focusManager.clearFocus()
                    true
                } else {
                    false
                }
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                textStyle = Smooch18,
                shape = RoundedCornerShape(15),
//                value = state.searchTxt,
                value = "Oskar",
                onValueChange = {
//                    update(
//                        state.copy(
//                            searchTxt = it
//                        )
//                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                label = {
                    Text(
                        stringResource(id = R.string.player_name),
                        style = Smooch14
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
//                                update(
//                                    state.copy(
//                                        searchTxt = ""
//                                    )
//                                )
                                focusManager.clearFocus()
                            })
                }
            )
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onCLickMenu() }
                    .size(35.dp),
            )
        }
    }
}

@Preview
@Composable
fun GamesHistorySearchRowPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            GamesHistorySearchRow(
//                state = PlayerListState()
            )
        }
    }
}