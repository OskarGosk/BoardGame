package com.goskar.boardgame.ui.games.addEditGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import org.koin.androidx.compose.koinViewModel

class AddEditGameScreen(val editGame: Game?) : Screen {

    @Composable
    override fun Content() {

        val viewModel: AddEditGameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(editGame) {
            if (editGame != null) {
                viewModel.update(
                    state.copy(
                        name = editGame.name,
                        expansion = editGame.expansion,
                        baseGame = editGame.baseGame,
                        minPlayer = editGame.minPlayer,
                        maxPlayer = editGame.maxPlayer,
                        games = editGame.games,
                        id = editGame.id
                    )
                )
            }
        }

        AddEditGameContent(
            state = state,
            update = viewModel::update,
            addGame = viewModel::validateAddGame,
            editGame = viewModel::validateEditGame
        )
    }
}


@Composable
fun AddEditGameContent(
    state: AddEditGameState,
    update: (AddEditGameState) -> Unit = {},
    addGame: () -> Unit = {},
    editGame: () -> Unit = {}
) {

    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            stringResource(id = R.string.new_game),
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(25.dp))


        OutlinedTextField(
            value = state.name ?: "",
            onValueChange = {
                update(
                    state.copy(
                        name = it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(stringResource(id = R.string.game_name))
            },
            singleLine = true
        )

        OutlinedTextField(
            value = state.minPlayer ?: "0",
            onValueChange = {
                update(
                    state.copy(
                        minPlayer = it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(stringResource(id = R.string.min_player))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = state.maxPlayer ?: "0",
            onValueChange = {
                update(
                    state.copy(
                        maxPlayer = it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(stringResource(id = R.string.max_player))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.expansion,
                onCheckedChange = {
                    update(
                        state.copy(
                            expansion = !state.expansion
                        )
                    )
                },
            )
            Text(stringResource(id = R.string.expansion))
        }
        if (state.expansion) {
            OutlinedTextField(
                value = state.baseGame ?: "",
                onValueChange = {
                    update(
                        state.copy(
                            baseGame = it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.base_game))
                },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                if (state.id == null) {
                    addGame()
                    if (state.successAddEditGame) navigator?.pop()
                } else {
                    editGame()
                    if (state.successAddEditGame) navigator?.pop()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.id == null) {
                    Text(
                        stringResource(id = R.string.add_board),
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_board),
                        modifier = Modifier.size(25.dp)
                    )
                } else {
                    Text(
                        stringResource(id = R.string.edit_game),
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit_game),
                        modifier = Modifier.size(25.dp)
                    )

                }
            }
        }

    }

}

@Preview
@Composable
fun addEditContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
//        AddEditGameContent()
    }
}