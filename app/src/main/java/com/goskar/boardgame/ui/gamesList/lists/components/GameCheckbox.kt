package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.Smooch18

@Composable
fun GameCheckbox(
    modifier: Modifier = Modifier,
    checkboxText: String,
    checked: Boolean,
    onCheckedChange: () -> Unit = {},
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Row(
            modifier = modifier
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange()
                })
            Text(
                text = checkboxText,
                style = Smooch18,
                modifier = Modifier
                    .padding(start = 5.dp)
            )
        }
    }
}


@Preview
@Composable
fun GameCheckboxPreview() {
    BoardGameTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            GameCheckbox(
                modifier = Modifier,
                checkboxText = "Expansion",
                checked = true
            )
        }
    }
}