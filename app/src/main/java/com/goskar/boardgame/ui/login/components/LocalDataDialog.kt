package com.goskar.boardgame.ui.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppActionDialog
import com.goskar.boardgame.ui.login.LocalDataSummary
import com.goskar.boardgame.ui.theme.BoardGameTheme

/** Asks what to do with the data found on the device when signing in to an account. */
@Composable
fun LocalDataDialog(
    summary: LocalDataSummary,
    onMerge: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
) {
    AppActionDialog(
        title = stringResource(R.string.login_local_data_title),
        message = stringResource(
            R.string.login_local_data_message,
            summary.gameCount,
            summary.playerCount,
            summary.sessionCount,
        ),
        primaryText = stringResource(R.string.login_local_data_merge),
        onPrimary = onMerge,
        secondaryText = stringResource(R.string.login_local_data_discard),
        onSecondary = onDiscard,
        onDismiss = onCancel,
    )
}

@Preview(name = "Local data — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun LocalDataDialogLightPreview() {
    BoardGameTheme(darkTheme = false) {
        LocalDataDialog(
            summary = LocalDataSummary(gameCount = 12, playerCount = 4, sessionCount = 40),
            onMerge = {},
            onDiscard = {},
            onCancel = {},
        )
    }
}

@Preview(name = "Local data — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun LocalDataDialogDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        LocalDataDialog(
            summary = LocalDataSummary(gameCount = 0, playerCount = 2, sessionCount = 0),
            onMerge = {},
            onDiscard = {},
            onCancel = {},
        )
    }
}
