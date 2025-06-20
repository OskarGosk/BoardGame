package com.goskar.boardgame.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
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
    BoardGameTheme {
        Navigator(screen = LoginScreen())
    }
}