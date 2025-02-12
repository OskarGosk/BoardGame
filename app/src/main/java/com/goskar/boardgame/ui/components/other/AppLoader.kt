package com.goskar.boardgame.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider

@Preview
@Composable
fun AppLoader() {
    Dialog(
        onDismissRequest = { },
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {

            CircularProgressIndicator(
                color = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 90.dp)
                    .size(35.dp),
            )
        }
    }
}