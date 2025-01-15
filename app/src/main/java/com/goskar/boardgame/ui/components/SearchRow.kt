@file:OptIn(ExperimentalComposeUiApi::class)

package com.goskar.boardgame.ui.components

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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

@Composable
fun SearchRow(
    label : Int? = null,
    value: String = "Search",
    onCLickMenu: () -> Unit = {},
    onSearch: (String) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .padding(10.dp)
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
            var text by remember { mutableStateOf(value) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    onSearch(text)
                },
                modifier = Modifier
                    .weight(1f),
                label = {
                        Text(stringResource(id = label?: R.string.empty))
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
                                text = ""
                            })
                }
            )
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onCLickMenu() }
                    .padding(start = 10.dp)
                    .size(35.dp),
            )
        }
    }
}

@Preview
@Composable
fun SearchRowPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SearchRow(
                label = R.string.player_name
            )
        }
    }
}