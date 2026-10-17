package com.goskar.boardgame.ui.screens.addPlayer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppGhostButton
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.screens.addPlayer.AddPlayerNewState
import com.goskar.boardgame.ui.theme.BoardGameTheme


@Composable
fun AddPlayerNewCard(
    modifier: Modifier = Modifier,
    state: AddPlayerNewState,
    onSelectAvatar: (Int) -> Unit,
    onNicknameChange: (String) -> Unit,
    onSelectSkill: (Int) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    AppListCard(modifier = modifier) {
        AppTextField(
            value = state.nickname,
            onValueChange = onNicknameChange,
            label = stringResource(R.string.addplayer_nickname_label),
            placeholder = stringResource(R.string.addplayer_nickname_hint),
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp),
                )
            },
        )

        Spacer(Modifier.height(24.dp))

        AppSectionHeader(title = stringResource(R.string.addplayer_skill_level))
        Spacer(Modifier.height(12.dp))
        AppOptionGrid(
            selected = state.selectedSkill,
            onSelect = onSelectSkill,
        )

        Spacer(Modifier.height(28.dp))

        AppPrimaryButton(
            text = if (state.isEditMode) stringResource(R.string.addplayer_save_changes) else stringResource(R.string.addplayer_save_new),
            onClick = onSave,
            leadingIcon = {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
        Spacer(Modifier.height(4.dp))
        AppGhostButton(text = stringResource(R.string.addplayer_discard), onClick = onDiscard)
    }
}


@Composable
private fun AddPlayerNewCardPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AddPlayerNewCard(
                state = AddPlayerNewState(nickname = "Alex"),
                onSelectAvatar = {},
                onNicknameChange = {},
                onSelectSkill = {},
                onSave = {},
                onDiscard = {},
            )
            AddPlayerNewCard(
                state = AddPlayerNewState(nickname = "Alex", isEditMode = true, selectedSkill = 1),
                onSelectAvatar = {},
                onNicknameChange = {},
                onSelectSkill = {},
                onSave = {},
                onDiscard = {},
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddPlayerNewCardLight() = BoardGameTheme(darkTheme = false) { AddPlayerNewCardPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddPlayerNewCardDark() = BoardGameTheme(darkTheme = true) { AddPlayerNewCardPreviewContent() }
