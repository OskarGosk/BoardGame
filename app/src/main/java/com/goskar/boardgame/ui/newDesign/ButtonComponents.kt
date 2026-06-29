package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// BUTTONS — Light
// =============================================================================

/**
 * Primary filled button — terracotta bg, white text.
 * Matches "Save Player", "Login", "Log Session".
 */
@Composable
fun BgPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = BoardGameShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = BoardGameColors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                leadingIcon(); Spacer(Modifier.width(8.dp))
            }
            Text(text, style = BoardGameTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

/**
 * Secondary outlined button — no fill, primary border.
 * Matches "Continue as Guest", "DISCARD CHANGES".
 */
@Composable
fun BgSecondaryButton(
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
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
    }
}

/**
 * Destructive button — soft red tint, red text.
 * Matches "Sign Out" on Profile screen.
 */
@Composable
fun BgDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = BoardGameShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = BoardGameColors.ErrorContainer,
            contentColor = BoardGameColors.Error,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        Text(text, style = BoardGameTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold))
    }
}

/**
 * FAB — circular, primary terracotta.
 * Matches the "+" FAB on Home, History, Collection.
 */
@Composable
fun BgFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = BoardGameColors.Primary,
        contentColor = BoardGameColors.OnPrimary,
        elevation = FloatingActionButtonDefaults.elevation(6.dp),
    ) { icon() }
}

// =============================================================================
// BUTTONS — Dark
// =============================================================================

/**
 * Primary button — solid orange (#E67E22), dark brown text.
 * Matches "Log Session", "Save Player" on dark screens.
 */
@Composable
fun BgDarkPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = BoardGameShapes.Full,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BoardGameDarkColors.OrangeFill,
            contentColor = BoardGameDarkColors.OnPrimary,
            disabledContainerColor = BoardGameDarkColors.SurfaceContainerHigh,
            disabledContentColor = BoardGameDarkColors.OnSurfaceVariant,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = BoardGameDarkColors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                leadingIcon(); Spacer(Modifier.width(8.dp))
            }
            Text(text, style = BoardGameDarkTypography.BodyLg.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

/**
 * Ghost button — transparent, muted label.
 * Matches "DISCARD CHANGES", "Cancel" on dark screens.
 */
@Composable
fun BgDarkGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = BoardGameDarkColors.OnSurfaceVariant),
    ) {
        Text(text.uppercase(), style = BoardGameDarkTypography.LabelLg)
    }
}

/**
 * Outlined secondary button — "Load More Players".
 */
@Composable
fun BgDarkSecondaryButton(
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
            .height(52.dp),
        shape = BoardGameShapes.Full,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            BoardGameDarkColors.SecondaryContainer
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = BoardGameDarkColors.OnSurfaceVariant,
            disabledContentColor = BoardGameDarkColors.OutlineVariant,
        ),
    ) {
        Text(text, style = BoardGameDarkTypography.LabelLg)
    }
}

/**
 * Icon-only add button — rounded square, brownish fill.
 * Matches small "+" buttons on dark game list rows.
 */
@Composable
fun BgDarkAddIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(BoardGameShapes.Medium)
            .background(BoardGameDarkColors.SecondaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { icon() }
}

/**
 * FAB — dark variant, orange fill.
 */
@Composable
fun BgDarkFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = BoardGameDarkColors.OrangeFill,
        contentColor = BoardGameDarkColors.OnPrimary,
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
    ) { icon() }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Buttons — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ButtonsLightPreview() {
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BgPrimaryButton(
                "Save Player", onClick = {},
                leadingIcon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp)) })
            BgPrimaryButton("Loading State", onClick = {}, loading = true)
            BgSecondaryButton("Continue as Guest", onClick = {})
            BgDestructiveButton("Sign Out", onClick = {})
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                BgFab(onClick = {}) { Icon(Icons.Default.Add, "Add") }
            }
        }
    }
}

@Preview(name = "Buttons — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ButtonsDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BgDarkPrimaryButton(
                "Log Session", onClick = {},
                leadingIcon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp)) })
            BgDarkSecondaryButton("Load More Players", onClick = {})
            BgDarkGhostButton("Discard Changes", onClick = {})
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                BgDarkFab(onClick = {}) { Icon(Icons.Default.Add, "Add") }
            }
        }
    }
}
