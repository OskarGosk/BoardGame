package com.goskar.boardgame.ui.screens.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppActionDialog
import com.goskar.boardgame.ui.screens.home.GuestRestoreOffer
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.utils.toLocalizedDateTime

/** Asks a guest whether to restore the backup that was found for this device. */
@Composable
fun GuestRestoreDialog(
    offer: GuestRestoreOffer,
    onRestore: () -> Unit,
    onDecline: () -> Unit,
) {
    AppActionDialog(
        title = stringResource(R.string.guest_restore_title),
        message = stringResource(
            R.string.guest_restore_message,
            offer.updatedAt.toLocalizedDateTime(),
            offer.gameCount,
            offer.sessionCount,
        ),
        primaryText = stringResource(
            if (offer.inProgress) R.string.guest_restore_in_progress else R.string.guest_restore_action
        ),
        onPrimary = onRestore,
        dismissText = stringResource(R.string.guest_restore_decline),
        onDismiss = onDecline,
        errorText = if (offer.failed) stringResource(R.string.guest_restore_error) else null,
        loading = offer.inProgress,
    )
}

private val PreviewOffer = GuestRestoreOffer(updatedAt = 1_760_000_000_000L, gameCount = 12, sessionCount = 40)

@Preview(name = "Offer — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GuestRestoreDialogLightPreview() {
    BoardGameTheme(darkTheme = false) {
        GuestRestoreDialog(offer = PreviewOffer, onRestore = {}, onDecline = {})
    }
}

@Preview(name = "Failed — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun GuestRestoreDialogFailedPreview() {
    BoardGameTheme(darkTheme = true) {
        GuestRestoreDialog(offer = PreviewOffer.copy(failed = true), onRestore = {}, onDecline = {})
    }
}

@Preview(name = "Restoring — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun GuestRestoreDialogRestoringPreview() {
    BoardGameTheme(darkTheme = false) {
        GuestRestoreDialog(offer = PreviewOffer.copy(inProgress = true), onRestore = {}, onDecline = {})
    }
}
