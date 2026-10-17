package com.goskar.boardgame.ui.screens.addGame.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSelectField
import com.goskar.boardgame.ui.components.AppSelectOption
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.screens.addGame.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun ManualEntryForm(
    modifier: Modifier = Modifier,
    state: AddGameNewState,
    onNameChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    onToggleCooperate: (Boolean) -> Unit,
    onToggleExpansion: (Boolean) -> Unit,
    onSelectBaseGame: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCoverPicked: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CoverPhotoPicker(coverUri = state.coverUri, onCoverPicked = onCoverPicked)

        AppTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.addgame_name_label),
            placeholder = stringResource(R.string.addgame_name_hint),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(
                value = state.minPlayers,
                onValueChange = onMinChange,
                label = stringResource(R.string.addgame_min_players),
                placeholder = "1",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                value = state.maxPlayers,
                onValueChange = onMaxChange,
                label = stringResource(R.string.addgame_max_players),
                placeholder = "4",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(
                value = state.category,
                onValueChange = onCategoryChange,
                label = stringResource(R.string.addgame_category_label),
                placeholder = stringResource(R.string.addgame_category_hint),
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                value = state.year,
                onValueChange = onYearChange,
                label = stringResource(R.string.addgame_year_label),
                placeholder = "2019",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
        }

        AppToggleRow(
            icon = Icons.Default.Groups,
            title = stringResource(R.string.addgame_coop_title),
            description = stringResource(R.string.addgame_coop_desc),
            checked = state.cooperate,
            onToggle = onToggleCooperate,
        )
        AppToggleRow(
            icon = Icons.Default.Extension,
            title = stringResource(R.string.addgame_exp_title),
            description = stringResource(R.string.addgame_exp_desc),
            checked = state.expansion,
            onToggle = onToggleExpansion,
        )

        if (state.expansion) {
            AppSelectField(
                label = stringResource(R.string.board_base),
                value = state.baseGame,
                placeholder = stringResource(R.string.addgame_base_hint),
                options = state.baseGameOptions.map { AppSelectOption(it, it) },
                onSelect = onSelectBaseGame,
            )
        }

        Spacer(Modifier.height(4.dp))
        AppPrimaryButton(
            text = if (state.isEditMode) stringResource(R.string.addgame_update) else stringResource(R.string.addgame_save),
            onClick = onSave,
            enabled = state.name.isNotBlank(),
        )
        Spacer(Modifier.height(8.dp))
    }
}


@Composable
private fun ManualEntryFormPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        ManualEntryForm(
            state = AddGameNewState(
                name = "Terraforming Mars",
                minPlayers = "1",
                maxPlayers = "5",
                expansion = true,
                baseGame = "Catan",
                baseGameOptions = listOf("Catan", "Dune: Imperium"),
                category = "Strategy",
                year = "2016",
            ),
            onNameChange = {},
            onMinChange = {},
            onMaxChange = {},
            onToggleCooperate = {},
            onToggleExpansion = {},
            onSelectBaseGame = {},
            onCategoryChange = {},
            onYearChange = {},
            onCoverPicked = {},
            onSave = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ManualEntryFormLight() = BoardGameTheme(darkTheme = false) { ManualEntryFormPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ManualEntryFormDark() = BoardGameTheme(darkTheme = true) { ManualEntryFormPreviewContent() }
