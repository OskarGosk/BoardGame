package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppTextField(
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
                modifier = Modifier.padding(bottom = 6.dp),
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

@Composable
private fun AppTextFieldPreviewContent() {
    AppTextField("", {}, Modifier.padding(16.dp), label = "Game Name", placeholder = "e.g. Terraforming Mars")
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AppTextFieldLight() = BoardGameTheme(darkTheme = false) { AppTextFieldPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AppTextFieldDark() = BoardGameTheme(darkTheme = true) { AppTextFieldPreviewContent() }
