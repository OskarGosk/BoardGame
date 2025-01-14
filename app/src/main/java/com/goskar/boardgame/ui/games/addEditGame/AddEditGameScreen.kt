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
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Game
import org.koin.androidx.compose.koinViewModel
import pl.ecp.app.ui.components.scaffold.BoardGameScaffold

class AddEditGameScreen(val editGame: Game?) : Screen {

    @Composable
    override fun Content() {

        val viewModel: AddEditGameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

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
        LaunchedEffect(state.successAddEditGame) {
            if (state.successAddEditGame) {
                navigator?.pop()
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

    BoardGameScaffold(
        titlePage = stringResource(id = if(state.name == null)R.string.new_game else R.string.edit_game)
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
                .padding(paddingValues)
        ) {
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
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                label = {
                    Text(stringResource(id = R.string.game_name))
                },
                singleLine = true
            )

            OutlinedTextField(
                value = state.minPlayer,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        update(
                            state.copy(
                                minPlayer = it
                            )
                        )
                    }
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
                value = state.maxPlayer,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        update(
                            state.copy(
                                maxPlayer = it
                            )
                        )
                    }
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
                Text(stringResource(id = R.string.is_expansion))
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
                shape = CutCornerShape(percent = 10),
                onClick = {
                    if (state.id == null) addGame() else editGame()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.inProgress
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
                            stringResource(id = R.string.edit_game_save),
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

}

@Preview
@Composable
fun AddEditContentPreview() {
    val state = AddEditGameState(
        name = "Marvel",
        expansion = true,
        baseGame = "Test",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,

        )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AddEditGameContent(state)
    }
}