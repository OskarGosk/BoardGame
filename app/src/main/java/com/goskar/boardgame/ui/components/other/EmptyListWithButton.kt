package com.goskar.boardgame.ui.components.other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun EmptyListWithButton(
    headerText: Int,
    infoText: Int,
    buttonText: Int,
    secondButtonText: Int? = null,
    onClick: () -> Unit = {},
    onClickSecondButton: () -> Unit = {}
) {
    Column {
        Text(
            stringResource(headerText),
            style = Smooch22,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        Text(
            stringResource(infoText),
            style = Smooch22,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = { onClick() },
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                Text(
                    stringResource(buttonText),
                    style = SmoochBold18
                )
            }

            if (secondButtonText != null) {
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = { onClickSecondButton() },
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                ) {
                    Text(
                        stringResource(secondButtonText),
                        style = SmoochBold18
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun EmptyListWithButtonPreview() {
    val navigator = LocalNavigator.current

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            EmptyListWithButton(
                headerText = R.string.board_empty_list,
                infoText = R.string.board_empty_list_add,
                buttonText = R.string.history_add,
                onClick = {
                    navigator?.push(AddEditGameScreen(null))
                }
            )
        }
    }
}

@Preview
@Composable
fun EmptyListWithTwoButtonPreview() {
    val navigator = LocalNavigator.current

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            EmptyListWithButton(
                headerText = R.string.board_empty_list,
                infoText = R.string.board_empty_list_add,
                buttonText = R.string.history_add,
                secondButtonText = R.string.board_add,
                onClick = {
                    navigator?.push(AddEditGameScreen(null))
                }
            )
        }
    }
}