package com.goskar.boardgame.ui.gameSearchBGG.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.SearchBGGListElements
import com.goskar.boardgame.ui.gameDetailsBGG.GameDetailsBGGScreen
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleBGGSearchRow(game: SearchBGGListElements){
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .sizeIn(maxHeight = 90.dp, minHeight = 48.dp)
            .clickable {
                navigator?.push(GameDetailsBGGScreen(game.id, game.name?:""))
            }
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end= 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(2.5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = game.name?:"",
                    style = SmoochBold26
                )
                Text(
                    text =  stringResource(R.string.bgg_name),
                    style = Smooch14
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = game.yearPublished.toString(),
                    style = SmoochBold26
                )
                Text(
                    text =  stringResource(R.string.bgg_year),
                    style = Smooch14,
                )
            }


        }
    }

}

@Preview (showBackground = true)
@Composable
fun SingleBGGSearchRowPreview() {
    val game = SearchBGGListElements(
        name = "Marvel Marvel Marvel Marvel Bardzo długa nazwa",
        yearPublished = 2023
    )
    SingleBGGSearchRow(game)
}

@Preview (showBackground = true)
@Composable
fun SingleBGGSearchRowPreview2() {
    val game = SearchBGGListElements(
        name = "Marvel",
        yearPublished = 2023
    )
    SingleBGGSearchRow(game)
}
