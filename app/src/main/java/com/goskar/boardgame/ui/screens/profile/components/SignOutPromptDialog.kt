package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppActionDialog
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.screens.profile.viewmodel.SignOutPrompt
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun SignOutPromptDialog(
    modifier: Modifier = Modifier,
    prompt: SignOutPrompt,
    isGuest: Boolean,
    onSyncAndSignOut: () -> Unit,
    onSignOutWithoutSync: () -> Unit,
    onDismiss: () -> Unit,
) {
    when (prompt) {
        SignOutPrompt.None -> Unit
        SignOutPrompt.UnsyncedData -> AppActionDialog(
            modifier = modifier,
            title = stringResource(R.string.sign_out_unsynced_title),
            message = stringResource(
                if (isGuest) R.string.sign_out_unsynced_message_guest else R.string.sign_out_unsynced_message_account
            ),
            primaryText = stringResource(R.string.sign_out_sync_and_sign_out),
            onPrimary = onSyncAndSignOut,
            secondaryText = stringResource(R.string.sign_out_anyway),
            onSecondary = onSignOutWithoutSync,
            onDismiss = onDismiss,
        )
        SignOutPrompt.SyncFailed -> AppActionDialog(
            modifier = modifier,
            title = stringResource(R.string.sign_out_sync_failed_title),
            message = stringResource(R.string.sign_out_sync_failed_message),
            primaryText = stringResource(R.string.sign_out_sync_and_sign_out),
            onPrimary = onSyncAndSignOut,
            secondaryText = stringResource(R.string.sign_out_anyway),
            onSecondary = onSignOutWithoutSync,
            onDismiss = onDismiss,
        )
    }
}


@Composable
private fun SignOutPromptDialogPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        SignOutPromptDialog(
            prompt = SignOutPrompt.UnsyncedData,
            isGuest = false,
            onSyncAndSignOut = {},
            onSignOutWithoutSync = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SignOutPromptDialogLight() = BoardGameTheme(darkTheme = false) { SignOutPromptDialogPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SignOutPromptDialogDark() = BoardGameTheme(darkTheme = true) { SignOutPromptDialogPreviewContent() }
