package com.goskar.boardgame.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun SignInToAccountCard(
    modifier: Modifier = Modifier,
    onSignInToAccount: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSecondaryButton(
            text = stringResource(R.string.profile_sign_in_to_account),
            onClick = onSignInToAccount,
        )
        Text(
            stringResource(R.string.profile_sign_in_to_account_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


@Composable
private fun SignInToAccountCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        SignInToAccountCard(onSignInToAccount = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SignInToAccountCardLight() = BoardGameTheme(darkTheme = false) { SignInToAccountCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SignInToAccountCardDark() = BoardGameTheme(darkTheme = true) { SignInToAccountCardPreviewContent() }
