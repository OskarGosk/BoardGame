package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.gamesList.addEditGame.AddEditGameScreen
import com.goskar.boardgame.ui.theme.Smooch22
import com.goskar.boardgame.ui.theme.SmoochBold18

@Composable
fun EmptyGameList(
) {
    val navigator = LocalNavigator.current

    Column {

        Text(
            stringResource(R.string.empty_game_list),
            style = Smooch22,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        Text(
            stringResource(R.string.add_more_game),
            style = Smooch22,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        Button(
            shape = CutCornerShape(percent = 10),
            onClick = { navigator?.push(AddEditGameScreen(null)) },
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                stringResource(R.string.add_board),
                style = SmoochBold18
            )
        }
    }
}

@Preview
@Composable
fun EmptyGameListPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            EmptyGameList()
        }
    }
}