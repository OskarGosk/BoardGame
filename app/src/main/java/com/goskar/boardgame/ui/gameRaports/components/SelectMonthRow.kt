package com.goskar.boardgame.ui.gameRaports.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.gameRaports.GameReportsState
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.utils.Months
import com.goskar.boardgame.utils.Months.Companion.getMonthByNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMonthRow(
    state: GameReportsState,
    update: (GameReportsState) -> Unit = {},
    modifier: Modifier,
    prepareChart: () -> Unit = {},

    ) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {

        Row(
            modifier = modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Month",
                textAlign = TextAlign.Start,
                style = Smooch18,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (state.selectedMonth) {
                        -1 -> "X"
                        0 -> "All"
                        else -> "${getMonthByNumber(state.selectedMonth)}"
                    },
                    style = SmoochBold18,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    Months.entries.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "$it",
                                    style = if (state.selectedMonth == it.monthsNumber) SmoochBold18 else Smooch18
                                )
                            },
                            onClick = {

                                if (state.selectedYear == 0) {
                                    Toast.makeText(
                                        context,
                                        "First select Year",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    expanded = false
                                } else {
                                    update(
                                        state.copy(
                                            selectedMonth = it.monthsNumber,
                                            selectedRowChartVariant = RowChartVariantsEnum.MONTH
                                        )
                                    )
                                    expanded = false
                                    prepareChart()
                                }
                            })

                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "All months",
                                style = if (state.selectedMonth == 0) SmoochBold18 else Smooch18
                            )
                        },
                        onClick = {
                            if (state.selectedYear == 0) {
                                Toast.makeText(
                                    context,
                                    "First select Year",
                                    Toast.LENGTH_LONG
                                ).show()
                                expanded = false
                            } else {
                                update(
                                    state.copy(
                                        selectedMonth = 0,
                                        selectedRowChartVariant = RowChartVariantsEnum.MONTHLY
                                    )
                                )
                                expanded = false
                                prepareChart()
                            }
                        })

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "X",
                                style = if (state.selectedMonth == -1) SmoochBold18 else Smooch18
                            )
                        },
                        onClick = {
                            update(
                                state.copy(
                                    selectedMonth = -1,
                                    selectedYear = 0,
                                    selectedRowChartVariant = RowChartVariantsEnum.YEAR
                                )
                            )
                            expanded = false
                            prepareChart()
                        })
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectMonthRowPreview() {
    BoardGameTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box() {
                SelectMonthRow(
                    state = GameReportsState(
                        minYear = 2015,
                        maxYear = 2025
                    ),
                    modifier = Modifier
                )
            }
        }
    }
}