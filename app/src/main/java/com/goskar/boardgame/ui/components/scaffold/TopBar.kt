package com.goskar.boardgame.ui.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.SmoochExtraBold32

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(titlePage: Int) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
    ) {
        TopAppBar(
            navigationIcon = {
                Text(
                    text = stringResource(titlePage),
                    style = SmoochExtraBold32,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            title = { // IGNORE
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
    TopBar(titlePage = R.string.app_name)
}