package com.goskar.boardgame.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

private val ErrorSpacing = 12.dp
private val ButtonSpacing = 8.dp

/**
 * Dialog with stacked full-width actions: a primary action, an optional secondary one and a cancel.
 * [errorText] is announced by screen readers as soon as it appears.
 */
@Composable
fun AppActionDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    primaryText: String,
    onPrimary: () -> Unit,
    onDismiss: () -> Unit,
    secondaryText: String? = null,
    onSecondary: () -> Unit = {},
    dismissText: String = stringResource(R.string.cancel),
    errorText: String? = null,
    loading: Boolean = false,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { if (!loading) onDismiss() },
        shape = BoardGameShapes.Large,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (errorText != null) {
                    Spacer(Modifier.height(ErrorSpacing))
                    Text(
                        errorText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ButtonSpacing),
            ) {
                AppPrimaryButton(
                    text = primaryText,
                    onClick = onPrimary,
                    enabled = !loading,
                    loading = loading,
                )
                if (secondaryText != null) {
                    AppSecondaryButton(text = secondaryText, onClick = onSecondary, enabled = !loading)
                }
                if (!loading) AppGhostButton(text = dismissText, onClick = onDismiss)
            }
        },
    )
}

@Preview(name = "Two actions — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppActionDialogTwoActionsLightPreview() {
    BoardGameTheme(darkTheme = false) {
        AppActionDialog(
            title = "Backup found",
            message = "Games: 12, sessions: 40",
            primaryText = "Restore",
            onPrimary = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Three actions — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppActionDialogThreeActionsDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        AppActionDialog(
            title = "Unsynced data",
            message = "Signing out removes your unsynced data from this device.",
            primaryText = "Sync and sign out",
            onPrimary = {},
            secondaryText = "Sign out anyway",
            onSecondary = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Error — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppActionDialogErrorPreview() {
    BoardGameTheme(darkTheme = false) {
        AppActionDialog(
            title = "Backup found",
            message = "Games: 12, sessions: 40",
            primaryText = "Restore",
            onPrimary = {},
            onDismiss = {},
            errorText = "Restoring failed. Check your connection and try again.",
        )
    }
}

@Preview(name = "Loading — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppActionDialogLoadingPreview() {
    BoardGameTheme(darkTheme = false) {
        AppActionDialog(
            title = "Backup found",
            message = "Games: 12, sessions: 40",
            primaryText = "Restoring…",
            onPrimary = {},
            onDismiss = {},
            loading = true,
        )
    }
}
