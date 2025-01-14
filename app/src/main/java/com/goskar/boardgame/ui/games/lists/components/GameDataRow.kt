package com.goskar.boardgame.ui.games.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R

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
            fontSize = 16.sp)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun GameDataRowPreview(){
//    Box {
//        GameDataRow(
//            header = R.string.min_player,
//            value = "4"
//        )
//    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        GameDataRow(
            header = R.string.min_player,
            value = "4"
        )    }
}