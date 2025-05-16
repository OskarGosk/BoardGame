package com.goskar.boardgame.ui.gameDetailsBGG.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.gameDetailsBGG.GameDetailsBGGState
import com.goskar.boardgame.ui.gamesList.addEditGame.components.DropdownBaseGame
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold22


@Composable
fun AddGameDialog(
    state: GameDetailsBGGState,
    allBaseGame: List<Game>,
    modifierButton: Modifier = Modifier,
    confirmButtonClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    update: (GameDetailsBGGState) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.board_add),
                style = SmoochBold22
            )
        },
        text = {

            Column {
                Text("Please check more info that we can't get from BGG.")

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.cooperate,
                        onCheckedChange = {
                            update(
                                state.copy(
                                    cooperate = !state.cooperate
                                )
                            )
                        },
                    )
                    Text(stringResource(id = R.string.board_is_cooperate), style = Smooch18)
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.expansion,
                        onCheckedChange = {
                            update(
                                state.copy(
                                    expansion = !state.expansion
                                )
                            )
                        },
                    )
                    Text(stringResource(id = R.string.board_is_expansion), style = Smooch18)
                }
                if (state.expansion) {
                    Row (modifier = Modifier.fillMaxWidth()){
                        DropdownBaseGame(allBaseGame, selectedName = state.baseGame, selectBaseGame = {
                            update(state.copy(
                                baseGame = it?.name,
                                baseGameId = it?.id
                            ))
                        })
                    }
                }
            }
        }, confirmButton = {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    confirmButtonClick()
                },
                modifier = modifierButton
                    .padding(top = 10.dp)
            ) {
                Text(
                    stringResource(R.string.board_new),
                    style = SmoochBold18
                )
            }
        },
        dismissButton = {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = { onDismiss() },
                modifier = modifierButton
                    .padding(top = 10.dp)
            ) {
                Text(
                    stringResource(R.string.back_to_list),
                    style = SmoochBold18
                )
            }
        }
    )
}

@Preview
@Composable
fun AddEditDialogPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            AddGameDialog(
                state = GameDetailsBGGState(
                    expansion = true
                ),
                allBaseGame = emptyList()
            )

        }
    }

}