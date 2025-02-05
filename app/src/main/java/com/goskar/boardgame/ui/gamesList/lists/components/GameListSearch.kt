package com.goskar.boardgame.ui.gamesList.lists.components

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gamesList.lists.GameListState
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.Smooch20
import com.goskar.boardgame.ui.theme.SmoochBold20
import com.goskar.boardgame.utils.SortList

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameSearchRow(
    onCLickMenu: () -> Unit = {},
    update: (GameListState) -> Unit = {},
    state: GameListState
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
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
                value = state.searchTxt,
                onValueChange = {
                    update(
                        state.copy(
                            searchTxt = it
                        )
                    )
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(1f),
                label = {
                    Text(
                        stringResource(id = R.string.board_name),
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
                                update(
                                    state.copy(
                                        searchTxt = ""
                                    )
                                )
                                focusManager.clearFocus()
                            })
                }
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    modifier = Modifier
                        .weight(1f)
                        .width(250.dp),
                    onDismissRequest = { expanded = false }) {
                    SortList.entries.forEach { sort ->
                        DropdownMenuItem(
                            text = {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Checkbox(
                                        checked = if (sort.value == state.sortOption) true else false,
                                        onCheckedChange = {
                                            update(
                                                state.copy(
                                                    sortOption = sort.value
                                                )
                                            )
                                            expanded = false
                                        }
                                    )
                                    Text(
                                        text = stringResource(id = sort.value),
                                        style = if (sort.value == state.sortOption) SmoochBold20 else Smooch20
                                    )
                                }
                            },
                            onClick = {
                                update(
                                    state.copy(
                                        sortOption = sort.value
                                    )
                                )
                                expanded = false
                            })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GameSearchRowPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            GameSearchRow(
                state = GameListState()
            )
        }
    }
}