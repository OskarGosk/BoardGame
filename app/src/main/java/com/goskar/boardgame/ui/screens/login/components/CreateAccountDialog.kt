package com.goskar.boardgame.ui.screens.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppActionDialog
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun CreateAccountDialog(
    modifier: Modifier = Modifier,
    contactEmail: String,
    onWriteEmail: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppActionDialog(
        modifier = modifier,
        title = stringResource(R.string.login_create_account_title),
        message = stringResource(R.string.login_create_account_message, contactEmail),
        primaryText = stringResource(R.string.login_create_account_write_email),
        onPrimary = onWriteEmail,
        onDismiss = onDismiss,
        dismissText = stringResource(R.string.ok),
    )
}


@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CreateAccountDialogLight() = BoardGameTheme(darkTheme = false) {
    CreateAccountDialog(contactEmail = "name@example.com", onWriteEmail = {}, onDismiss = {})
}

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CreateAccountDialogDark() = BoardGameTheme(darkTheme = true) {
    CreateAccountDialog(contactEmail = "name@example.com", onWriteEmail = {}, onDismiss = {})
}
