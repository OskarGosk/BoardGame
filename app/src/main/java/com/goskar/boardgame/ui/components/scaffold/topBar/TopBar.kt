package com.goskar.boardgame.ui.components.scaffold.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.SmoochExtraBold32

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    titlePage: String,
    showSynchronizedIcon: Boolean = true,
    state: TopBarState,
    uploadDataToFirebase: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = titlePage,
                    style = SmoochExtraBold32,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            actions = {
                if (showSynchronizedIcon) {
                    IconButton(
                        onClick = {
                            uploadDataToFirebase()
                        }
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .padding(bottom = 90.dp)
                                    .size(35.dp),
                            )
                        } else {
                            Icon(
                                imageVector = if (state.isSuccess) Icons.Filled.Refresh else Icons.Filled.Warning,
                                contentDescription = "LogOut",
                                tint = if (state.isSuccess) Color.Black else Color.Red,
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(titlePage = "Test", state = TopBarState())
}