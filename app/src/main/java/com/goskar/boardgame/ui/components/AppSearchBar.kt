package com.goskar.boardgame.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = cs.outline) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                null,
                tint = if (onSearch != null) cs.primary else cs.outline,
                modifier = if (onSearch != null) Modifier.clickable { onSearch() } else Modifier,
            )
        },
        trailingIcon = trailingIcon,
        keyboardOptions = if (onSearch != null) {
            KeyboardOptions(imeAction = ImeAction.Search)
        } else {
            KeyboardOptions.Default
        },
        keyboardActions = if (onSearch != null) {
            KeyboardActions(onSearch = { onSearch() })
        } else {
            KeyboardActions.Default
        },
        singleLine = true,
        shape = BoardGameShapes.Full,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = cs.primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = cs.surfaceContainerLow,
            unfocusedContainerColor = cs.surfaceContainerLow,
            cursorColor = cs.primary,
            focusedTextColor = cs.onSurface,
            unfocusedTextColor = cs.onSurface,
        ),
    )
}

@Composable
private fun AppSearchBarPreviewContent() {
    var text by remember { mutableStateOf("") }
    AppSearchBar(text, { text = it }, Modifier.padding(16.dp), onSearch = {})
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSearchBarLight() = BoardGameTheme(darkTheme = false) { AppSearchBarPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSearchBarDark() = BoardGameTheme(darkTheme = true) { AppSearchBarPreviewContent() }
