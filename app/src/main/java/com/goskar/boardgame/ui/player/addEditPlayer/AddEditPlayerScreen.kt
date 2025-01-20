package com.goskar.boardgame.ui.player.addEditPlayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.models.Player
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import java.util.UUID

class AddEditPlayerScreen(val editPlayer: Player?) : Screen {
    @Composable
    override fun Content() {

        val viewModel: AddEditPlayerViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current
        LaunchedEffect(editPlayer) {
            viewModel.update(
                state.copy(
                    name = editPlayer?.name ?: "",
                    games = editPlayer?.games ?: 0,
                    winRatio = editPlayer?.winRatio ?: 0,
                    description = editPlayer?.description ?: "",
                    selected = editPlayer?.selected ?: false,
                    id = editPlayer?.id ?: UUID.randomUUID().toString()
                )
            )
        }
        LaunchedEffect(state.successAddEditPlayer) {
            if (state.successAddEditPlayer) navigator?.pop()
        }
        AddEditPlayerContent(
            newPlayer = editPlayer == null,
            state = state,
            viewModel::update,
            viewModel::validateAddPLayer,
            viewModel::validateEditPlayer
        )
    }
}

@Composable
fun AddEditPlayerContent(
    newPlayer: Boolean,
    state: AddEditPLayerState,
    update: (AddEditPLayerState) -> Unit = {},
    addPlayer: () -> Unit = {},
    editPlayer: () -> Unit = {}
) {
    BoardGameScaffold(
        titlePage = stringResource( id = if (newPlayer)R.string.new_player else R.string.edit_player)
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(10.dp)
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                textStyle = Smooch18,
                value = state.name,
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
                    Text(stringResource(id = R.string.player_name),
                        style = Smooch14)
                },
                singleLine = true
            )


            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                textStyle = Smooch18,
                value = state.description,
                onValueChange = {
                    update(
                        state.copy(
                            description = it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_description),
                        style = Smooch14)
                })
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (newPlayer) addPlayer() else editPlayer()
                },
                modifier = Modifier.fillMaxWidth()
                    .size(40.dp),
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(id = if(newPlayer) R.string.add_player else R.string.edit_player_save),
                        style = SmoochBold18
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_player),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AddEditPlayerContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        AddEditPlayerContent(newPlayer = false, state = AddEditPLayerState(name = "Oskar",
            games = 3,
            winRatio = 12,
            description = "Testowy",
            selected = true,
            id = UUID.randomUUID().toString()))
    }
}