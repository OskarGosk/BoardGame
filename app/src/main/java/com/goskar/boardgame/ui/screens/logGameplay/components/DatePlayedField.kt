package com.goskar.boardgame.ui.screens.logGameplay.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.goskar.boardgame.ui.components.AppDropdownField
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePlayedField(
    modifier: Modifier = Modifier,
    value: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    AppDropdownField(
        modifier = modifier,
        label = stringResource(R.string.log_date_played),
        value = value,
        onClick = { showDialog = true })

    if (showDialog) {
        val initialMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            onDateSelected(selected)
                        }
                        showDialog = false
                    },
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) { Text(stringResource(R.string.cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
private fun DatePlayedFieldPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        DatePlayedField(value = "10/16/2026", date = LocalDate.of(2026, 10, 16), onDateSelected = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun DatePlayedFieldLight() = BoardGameTheme(darkTheme = false) { DatePlayedFieldPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun DatePlayedFieldDark() = BoardGameTheme(darkTheme = true) { DatePlayedFieldPreviewContent() }
