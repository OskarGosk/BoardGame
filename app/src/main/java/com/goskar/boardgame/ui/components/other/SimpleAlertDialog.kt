package com.goskar.boardgame.ui.components.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.SmoochBold22

@Composable
fun SimpleAlertDialog(
    modifierButton: Modifier = Modifier,
    titleText: String,
    contentText: Int,
    confirmButtonClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = titleText,
                style = SmoochBold22
            )
        },
        text = {
            Text(
                stringResource(contentText),
                style = Smooch16
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
                    stringResource(R.string.confirm),
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
fun SimpleAlertDialogPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            SimpleAlertDialog(
                titleText = stringResource(R.string.delete, "Oskar"),
                contentText = R.string.board_delete_info
            )
        }
    }

}
