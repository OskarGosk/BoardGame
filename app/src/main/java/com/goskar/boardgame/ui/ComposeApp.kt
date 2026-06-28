package com.goskar.boardgame.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.goskar.boardgame.ui.components.other.AppSnackbar
import com.goskar.boardgame.ui.components.other.AppSnackbarController
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.login.LoginScreen
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun ComposeApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val controller = remember { AppSnackbarController(snackbarHostState, scope) }
    BoardGameTheme {
        // Single, app-wide snackbar host rendered at the very top, overlaying the
        // whole app (does not push content). Any screen shows messages via
        // LocalSnackbarHost.current.show(...).
        CompositionLocalProvider(LocalSnackbarHost provides controller) {
            Box(modifier = Modifier.fillMaxSize()) {
                Navigator(screen = LoginScreen())
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                ) { data -> AppSnackbar(snackbarData = data) }
            }
        }
    }
}
