package com.goskar.boardgame.ui.components.playerList.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.Smooch16


@SuppressLint("DefaultLocale")
@Composable
fun WinLinearIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    colorPrimary: Color = Color.Red,
    colorBackground: Color = Color.Black,
    isExpanded: Boolean,
) {
    Column {
        LinearProgressIndicator(
            progress = {
                progress
            },
            strokeCap = StrokeCap.Square,
            gapSize = 0.dp,

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = if (isExpanded) 0.dp else 10.dp),
            color = colorPrimary,
            trackColor = colorBackground,
            drawStopIndicator = {
                ProgressIndicatorDefaults.drawStopIndicator(
                    drawScope = this,
                    stopSize = 0.dp,
                    color = Color.Red,
                    strokeCap = StrokeCap.Square
                )
            }
        )
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (!progress.isNaN()) {
                    Text(text = String.format("Win - %.2f%%", progress * 100), style = Smooch16)
                    Text(
                        text = String.format("Lose - %.2f%%", (1 - progress) * 100),
                        style = Smooch16
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun WinLinearIndicatorPreview() {
    Surface {
        Box(modifier = Modifier.padding(10.dp)) {
            WinLinearIndicator(
                0.4f,
                modifier = Modifier,
                isExpanded = false
            )
        }
    }
}

@Preview
@Composable
fun WinLinearIndicatorExpandedPreview() {
    Surface {
        Box(modifier = Modifier.padding(10.dp)) {
            WinLinearIndicator(
                0.478f,
                modifier = Modifier,
                isExpanded = true
            )
        }
    }
}