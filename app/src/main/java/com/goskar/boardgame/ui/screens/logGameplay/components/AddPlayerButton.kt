package com.goskar.boardgame.ui.screens.logGameplay.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.screens.logGameplay.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun AddPlayerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val outline = MaterialTheme.colorScheme.outline
    Box(
        modifier = modifier
            .size(44.dp)
            .drawBehind {
                drawCircle(
                    color = outline,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                    ),
                )
            }
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = stringResource(R.string.cd_add_player),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}


@Composable
private fun AddPlayerButtonPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        AddPlayerButton(onClick = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddPlayerButtonLight() = BoardGameTheme(darkTheme = false) { AddPlayerButtonPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddPlayerButtonDark() = BoardGameTheme(darkTheme = true) { AddPlayerButtonPreviewContent() }
