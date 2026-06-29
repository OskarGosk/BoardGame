package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =============================================================================
// INPUTS — Light
// =============================================================================

/**
 * Standard text field — warm fill, 12dp radius, terracotta focus border.
 * Matches "Player Nickname", email, password fields.
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
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

/**
 * Search bar — pill shape, muted placeholder, leading search icon.
 * Matches search bars on Home and Add Game screens.
 */
@Composable
fun BgSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                placeholder,
                style = BoardGameTypography.BodyLg,
                color = BoardGameColors.Outline
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = BoardGameColors.Outline) },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = BoardGameShapes.Full,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BoardGameColors.Primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = BoardGameColors.SurfaceContainerLow,
            unfocusedContainerColor = BoardGameColors.SurfaceContainerLow,
            cursorColor = BoardGameColors.Primary,
            focusedTextColor = BoardGameColors.OnSurface,
            unfocusedTextColor = BoardGameColors.OnSurface,
        ),
    )
}

/**
 * Multiline textarea — session notes, light variant.
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
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = (minLines * 52).dp),
            placeholder = {
                Text(
                    placeholder,
                    style = BoardGameTypography.BodySm,
                    color = BoardGameColors.Outline
                )
            },
            singleLine = false,
            minLines = minLines,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoardGameColors.Primary,
                unfocusedBorderColor = BoardGameColors.OutlineVariant,
                focusedContainerColor = BoardGameColors.SurfaceContainerLowest,
                unfocusedContainerColor = BoardGameColors.SurfaceContainerLow,
                cursorColor = BoardGameColors.Primary,
                focusedTextColor = BoardGameColors.OnSurface,
                unfocusedTextColor = BoardGameColors.OnSurface,
            ),
        )
    }
}

// =============================================================================
// INPUTS — Dark
// =============================================================================

/**
 * Dark text field — #1A1A1A fill, #333 border, orange focus ring.
 * Matches "Enter name…", email, password on dark screens.
 */
@Composable
fun BgDarkTextField(
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
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label.uppercase(), style = BoardGameDarkTypography.LabelLg,
                color = BoardGameDarkColors.OnSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    placeholder,
                    style = BoardGameDarkTypography.BodyLg,
                    color = BoardGameDarkColors.Outline
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoardGameDarkColors.Primary,
                unfocusedBorderColor = BoardGameDarkColors.CardBorder,
                errorBorderColor = BoardGameDarkColors.Error,
                focusedContainerColor = BoardGameDarkColors.InputBg,
                unfocusedContainerColor = BoardGameDarkColors.InputBg,
                cursorColor = BoardGameDarkColors.Primary,
                focusedTextColor = BoardGameDarkColors.OnSurface,
                unfocusedTextColor = BoardGameDarkColors.OnSurface,
                focusedLeadingIconColor = BoardGameDarkColors.OnSurfaceVariant,
                unfocusedLeadingIconColor = BoardGameDarkColors.Outline,
            ),
        )
    }
}

/**
 * Dark search bar — pill, dark fill, muted placeholder.
 */
@Composable
fun BgDarkSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                placeholder,
                style = BoardGameDarkTypography.BodyLg,
                color = BoardGameDarkColors.Outline
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = BoardGameDarkColors.Outline) },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = BoardGameShapes.Full,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BoardGameDarkColors.Primary,
            unfocusedBorderColor = BoardGameDarkColors.CardBorder,
            focusedContainerColor = BoardGameDarkColors.SurfaceContainerLow,
            unfocusedContainerColor = BoardGameDarkColors.SurfaceContainerLow,
            cursorColor = BoardGameDarkColors.Primary,
            focusedTextColor = BoardGameDarkColors.OnSurface,
            unfocusedTextColor = BoardGameDarkColors.OnSurface,
        ),
    )
}

/**
 * Dropdown selector — "SELECT GAME", "DATE PLAYED" on Log Session screen.
 */
@Composable
fun BgDarkDropdownField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select…",
) {
    Column(modifier = modifier) {
        Text(
            label.uppercase(), style = BoardGameDarkTypography.LabelLg,
            color = BoardGameDarkColors.OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(BoardGameShapes.Medium)
                .background(BoardGameDarkColors.SurfaceContainer)
                .border(1.dp, BoardGameDarkColors.CardBorder, BoardGameShapes.Medium)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = value.ifBlank { placeholder },
                style = BoardGameDarkTypography.BodyLg,
                color = if (value.isBlank()) BoardGameDarkColors.Outline else BoardGameDarkColors.OnSurface
            )
            Icon(Icons.Default.KeyboardArrowDown, null, tint = BoardGameDarkColors.Outline)
        }
    }
}

/**
 * Dark multiline textarea — session notes.
 */
@Composable
fun BgDarkTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    minLines: Int = 4,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                label.uppercase(), style = BoardGameDarkTypography.LabelLg,
                color = BoardGameDarkColors.OnSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = (minLines * 52).dp),
            placeholder = {
                Text(
                    placeholder,
                    style = BoardGameDarkTypography.BodyMd,
                    color = BoardGameDarkColors.Outline
                )
            },
            singleLine = false,
            minLines = minLines,
            shape = BoardGameShapes.Medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoardGameDarkColors.Primary,
                unfocusedBorderColor = BoardGameDarkColors.CardBorder,
                focusedContainerColor = BoardGameDarkColors.SurfaceContainer,
                unfocusedContainerColor = BoardGameDarkColors.SurfaceContainer,
                cursorColor = BoardGameDarkColors.Primary,
                focusedTextColor = BoardGameDarkColors.OnSurface,
                unfocusedTextColor = BoardGameDarkColors.OnSurface,
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
    BoardGameTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var text by remember { mutableStateOf("") }
            BgSearchBar(
                value = text,
                onValueChange = { text = it },
                placeholder = "Search BGG database…"
            )
            BgTextField(
                value = "",
                onValueChange = {},
                label = "Player Nickname",
                placeholder = "e.g. DungeonMaster99",
                trailingIcon = {
                    Icon(
                        Icons.Default.Email,
                        null,
                        tint = BoardGameColors.Outline,
                        modifier = Modifier.size(18.dp)
                    )
                })
            BgTextArea(
                value = "", onValueChange = {}, label = "Session Notes",
                placeholder = "How did the session go?", minLines = 3
            )
        }
    }
}

@Preview(name = "Inputs — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun InputsDarkPreview() {
    BoardGameDarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var text by remember { mutableStateOf("") }
            BgDarkSearchBar(
                value = text,
                onValueChange = { text = it },
                placeholder = "Search collection…"
            )
            BgDarkTextField(
                value = "",
                onValueChange = {},
                label = "Player Nickname",
                placeholder = "Enter name…"
            )
            BgDarkDropdownField(label = "Select Game", value = "Dune: Imperium", onClick = {})
            BgDarkTextArea(
                value = "", onValueChange = {}, label = "Session Notes",
                placeholder = "Briefly describe the highlights…", minLines = 3
            )
        }
    }
}
