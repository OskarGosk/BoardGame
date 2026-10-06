package com.goskar.boardgame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

private const val BGG_URL = "https://boardgamegeek.com"

/**
 * BoardGameGeek attribution required by the BGG XML API terms of use.
 * Show this on any screen that displays data sourced from BGG.
 */
@Composable
fun AppPoweredByBgg(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.bgg),
            contentDescription = "Powered by BoardGameGeek",
            contentScale = ContentScale.Fit,
            // light backdrop keeps the dark BGG wordmark legible on the dark theme too
            modifier = Modifier
                .clip(BoardGameShapes.Full)
                .background(Color.White)
                .clickable { uriHandler.openUri(BGG_URL) }
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .height(20.dp),
        )
    }
}

@Preview(name = "Powered by BGG — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppPoweredByBggLight() = BoardGameTheme(darkTheme = false) { AppPoweredByBgg() }

@Preview(name = "Powered by BGG — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppPoweredByBggDark() = BoardGameTheme(darkTheme = true) { AppPoweredByBgg() }
