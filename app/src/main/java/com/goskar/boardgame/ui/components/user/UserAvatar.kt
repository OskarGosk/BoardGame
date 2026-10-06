package com.goskar.boardgame.ui.components.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Resolves the logged-in user's avatar initials from the single shared source, so every
 * top bar shows the same avatar. Call from a Screen's Content() and pass the value into the
 * stateless content composable (keeps @Preview free of Koin).
 */
@Composable
fun rememberUserInitials(): String {
    val viewModel: CurrentUserViewModel = koinViewModel()
    val initials by viewModel.initials.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    return initials
}
