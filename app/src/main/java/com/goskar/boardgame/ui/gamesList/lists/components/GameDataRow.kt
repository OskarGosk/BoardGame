package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold16

@Composable
fun GameDataRow(
    header: Int,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            stringResource(header),
            style = Smooch16
        )
        Text(
            text = value,
            style = SmoochBold16
        )
    }
}

@Preview
@Composable
fun GameDataRowPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        GameDataRow(
            header = R.string.min_player,
            value = "4"
        )
    }
}