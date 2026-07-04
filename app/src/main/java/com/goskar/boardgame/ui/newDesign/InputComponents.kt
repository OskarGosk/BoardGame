package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =============================================================================
// INPUTS — theme-aware (single MaterialTheme-based set)
// =============================================================================

/**
 * Standard text field — rounded, primary focus border, error support.
 */
@Composable
fun BgTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label, style = MaterialTheme.typography.labelMedium,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = cs.outline) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = cs.primary,
                unfocusedBorderColor = cs.outlineVariant,
                errorBorderColor = cs.error,
                focusedContainerColor = cs.surfaceContainerLowest,
                unfocusedContainerColor = cs.surfaceContainerLow,
                cursorColor = cs.primary,
                focusedTextColor = cs.onSurface,
                unfocusedTextColor = cs.onSurface,
                focusedLeadingIconColor = cs.onSurfaceVariant,
                unfocusedLeadingIconColor = cs.outline,
            ),
        )
        if (supportingText != null) {
            Text(supportingText, style = MaterialTheme.typography.labelMedium, color = cs.error)
        }
    }
}

/**
 * Search bar — pill shape, muted placeholder, leading search icon.
 */
@Composable
fun BgSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = cs.outline) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = cs.outline) },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = BoardGameShapes.Full,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = cs.primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = cs.surfaceContainerLow,
            unfocusedContainerColor = cs.surfaceContainerLow,
            cursorColor = cs.primary,
            focusedTextColor = cs.onSurface,
            unfocusedTextColor = cs.onSurface,
        ),
    )
}

/**
 * Multiline textarea — session notes.
 */
@Composable
fun BgTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    minLines: Int = 4,
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label, style = MaterialTheme.typography.labelMedium,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = (minLines * 52).dp),
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = cs.outline) },
            singleLine = false,
            minLines = minLines,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = cs.primary,
                unfocusedBorderColor = cs.outlineVariant,
                focusedContainerColor = cs.surfaceContainerLowest,
                unfocusedContainerColor = cs.surfaceContainerLow,
                cursorColor = cs.primary,
                focusedTextColor = cs.onSurface,
                unfocusedTextColor = cs.onSurface,
            ),
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Inputs — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun InputsLightPreview() {
    BoardGameTheme(darkTheme = false) { InputsPreviewContent() }
}

@Preview(name = "Inputs — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun InputsDarkPreview() {
    BoardGameTheme(darkTheme = true) { InputsPreviewContent() }
}

@Composable
private fun InputsPreviewContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        var text by remember { mutableStateOf("") }
        BgSearchBar(value = text, onValueChange = { text = it }, placeholder = "Search BGG database…")
        BgTextField(value = "", onValueChange = {}, label = "Game Name", placeholder = "e.g. Terraforming Mars")
        BgTextArea(value = "", onValueChange = {}, label = "Notes", placeholder = "Describe the session…")
    }
}
