package com.goskar.boardgame.ui.gamesList.addEditGame.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBaseGame(
    baseGameList: List<Game>,
    selectBaseGame: (Game?) -> Unit = {},
    selectedName: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            textStyle = Smooch18,
            value = if (selectedName.isNullOrEmpty()) stringResource(R.string.board_base_select) else selectedName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = {
                Text(
                    stringResource(id = R.string.board_base),
                    style = Smooch14
                )
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.board_base_select),
                        style = if(selectedName.isNullOrEmpty()) SmoochBold18 else Smooch18
                    )
                },
                onClick = {
                    selectBaseGame(null)
                    expanded = !expanded
                }
            )
            baseGameList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.name,
                            style = if(it.name == selectedName) SmoochBold18 else Smooch18
                        )
                    },
                    onClick = {
                        selectBaseGame(it)
                        expanded = !expanded
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownBaseGamePreview() {

    val game = Game(
        name = "Marvel",
        expansion = true,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 0,
        uriFromBgg = null,
        id = UUID.randomUUID().toString()
    )
    Box(modifier = Modifier.fillMaxSize()) {
        DropdownBaseGame(listOf(game, game), selectedName = game.name)
    }
}