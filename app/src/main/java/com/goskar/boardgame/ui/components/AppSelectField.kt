package com.goskar.boardgame.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.BoardGameTheme

/** Pozycja listy w [AppSelectField]: [id] trafia do callbacku, [label] jest wyświetlany. */
data class AppSelectOption(val id: String, val label: String)

/** Pole z listą rozwijaną (etykieta, aktualna wartość lub placeholder, menu z opcjami). */
@Composable
fun AppSelectField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    options: List<AppSelectOption>,
    onSelect: (String) -> Unit,
    placeholder: String = stringResource(R.string.dropdown_select),
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        AppDropdownField(
            label = label,
            value = value,
            placeholder = placeholder,
            onClick = { if (options.isNotEmpty()) expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelect(option.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AppSelectFieldPreviewContent() {
    val options = listOf(AppSelectOption("1", "Dune: Imperium"), AppSelectOption("2", "Catan"))
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppSelectField(label = "Select Game", value = "", options = options, onSelect = {})
        AppSelectField(label = "Select Game", value = "Dune: Imperium", options = options, onSelect = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSelectFieldLight() = BoardGameTheme(darkTheme = false) { AppSelectFieldPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSelectFieldDark() = BoardGameTheme(darkTheme = true) { AppSelectFieldPreviewContent() }
