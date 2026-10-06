package com.goskar.boardgame.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppProgressBar
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun WinRatioCard(
    value: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    AppListCard(modifier = modifier) {
        Text(
            stringResource(R.string.home_win_ratio),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(10.dp))
        AppProgressBar(progress = progress)
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun WinRatioCardLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) {
            WinRatioCard(
                value = "65%",
                progress = 0.65f
            )
        }
    }
}

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun WinRatioCardDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Box(Modifier.padding(16.dp)) {
            WinRatioCard(
                value = "42%",
                progress = 0.42f
            )
        }
    }
}
