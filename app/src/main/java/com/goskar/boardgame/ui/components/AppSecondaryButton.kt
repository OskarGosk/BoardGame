package com.goskar.boardgame.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = BoardGameShapes.Medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun AppSecondaryButtonPreviewContent() {
    AppSecondaryButton("Continue as Guest", onClick = {}, modifier = Modifier.padding(16.dp))
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppSecondaryButtonLight() =
    BoardGameTheme(darkTheme = false) { AppSecondaryButtonPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppSecondaryButtonDark() =
    BoardGameTheme(darkTheme = true) { AppSecondaryButtonPreviewContent() }
