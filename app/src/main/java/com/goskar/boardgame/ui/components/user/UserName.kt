package com.goskar.boardgame.ui.components.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.goskar.boardgame.R

/** Nazwa użytkownika bez wpisanego na stałe tekstu: ViewModel wybiera wariant, UI mapuje go na tekst. */
sealed interface UserName {
    data class Named(val value: String) : UserName
    data object Guest : UserName
    data object Player : UserName
}

@Composable
fun UserName.text(): String = when (this) {
    is UserName.Named -> value
    UserName.Guest -> stringResource(R.string.user_name_guest)
    UserName.Player -> stringResource(R.string.user_name_player)
}
