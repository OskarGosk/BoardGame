package com.goskar.boardgame.ui.playerList.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.ui.playerList.PlayerListState
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold22
import java.util.UUID

@Composable
fun AddEditDialog(
    newPlayer: Boolean,
    state: PlayerListState,
    modifierButton: Modifier = Modifier,
    confirmButtonClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    update: (PlayerListState) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(if (newPlayer)R.string.player_new else R.string.player_edit,),
                style = SmoochBold22
            )
        },
        text = {
            OutlinedTextField(
                textStyle = Smooch18,
                value = state.player?.name?:"",
                onValueChange = {
                    update(
                        state.copy(
                            player = Player(
                                name = it,
                                games = state.player?.games?:0,
                                winRatio = state.player?.winRatio?:0,
                                description = "",
                                selected = false,
                                id = state.player?.id?: UUID.randomUUID().toString()
                            )
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_name),
                        style = Smooch14
                    )
                },
                singleLine = true
            )
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
                    stringResource(R.string.save),
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
            AddEditDialog(
                newPlayer = false,
                state = PlayerListState(
                    player = Player( name = "Oskar",
                    games = 3,
                    winRatio = 12,
                    description = "Testowy",
                    selected = true,
                    id = UUID.randomUUID().toString()))
            )

        }
    }

}