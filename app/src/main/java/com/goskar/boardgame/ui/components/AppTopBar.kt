package com.goskar.boardgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun AppTopBar(
    title: String? = stringResource(R.string.app_name_title),
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = BoardGameSpacing.MarginMobile),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        } else if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(4.dp))
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title ?: stringResource(R.string.app_name_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            subtitle?.let {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun AppTopBarPreviewContent() {
    AppTopBar(
        stringResource(R.string.players_title),
        stringResource(R.string.players_subtitle),
        onBack = {})
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppTopBarLight() = BoardGameTheme(darkTheme = false) { AppTopBarPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppTopBarDark() = BoardGameTheme(darkTheme = true) { AppTopBarPreviewContent() }
