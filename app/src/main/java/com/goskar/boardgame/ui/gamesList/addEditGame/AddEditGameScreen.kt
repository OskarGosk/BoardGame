package com.goskar.boardgame.ui.gamesList.addEditGame

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.Constants.GLOBAL_TAG
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.components.other.CameraView
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.BottomBarElements
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.utils.checkAndRequestPermissionWithClick
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
                        cooperate = editGame.cooperate,
                        baseGame = editGame.baseGame,
                        minPlayer = editGame.minPlayer,
                        maxPlayer = editGame.maxPlayer,
                        games = editGame.games,
                        uri = editGame.uri ?: "",
                        id = editGame.id
                    )
                )
            }
        }
        LaunchedEffect(state.successAddEditGame) {
            if (state.successAddEditGame) {
                navigator?.replace(GameListScreen())
            }
        }

        AddEditGameContent(
            state = state,
            update = viewModel::update,
            addEditGame = viewModel::validateAddEitGame,
            takePhoto = viewModel::takePhoto
        )
    }
}


@Composable
fun AddEditGameContent(
    state: AddEditGameState,
    update: (AddEditGameState) -> Unit = {},
    addEditGame: (Context) -> Unit = {},
    takePhoto: (String, ImageCapture, File, Executor, (Uri) -> Unit, (ImageCaptureException) -> Unit) -> Unit = { _, _, _, _, _, _ -> }
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var shouldOpenCamera by remember { mutableStateOf(false) }
    val permission = Manifest.permission.CAMERA

    val outputDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldOpenCamera = true
        } else {
            // Show dialog
            Toast.makeText(context, R.string.camera_denied, Toast.LENGTH_LONG).show()
        }
    }

    BoardGameScaffold(
        titlePage = stringResource(if (state.name == null) R.string.board_new else R.string.board_edit),
        selectedScreen = BottomBarElements.GameListButton.title
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                textStyle = Smooch18,
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
                    Text(
                        stringResource(id = R.string.board_name),
                        style = Smooch14
                    )
                },
                singleLine = true
            )

            OutlinedTextField(
                textStyle = Smooch18,
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
                    Text(
                        stringResource(id = R.string.board_min_player),
                        style = Smooch14
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                textStyle = Smooch18,
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
                    Text(
                        stringResource(id = R.string.board_max_player),
                        style = Smooch14
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.cooperate,
                    onCheckedChange = {
                        update(
                            state.copy(
                                cooperate = !state.cooperate
                            )
                        )
                    },
                )
                Text(stringResource(id = R.string.board_is_cooperate), style = Smooch18)
            }

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
                Text(stringResource(id = R.string.board_is_expansion), style = Smooch18)
            }
            if (state.expansion) {
                OutlinedTextField(
                    textStyle = Smooch18,
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
                        Text(
                            stringResource(id = R.string.board_base),
                            style = Smooch14
                        )
                    },
                    singleLine = true
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                SinglePhotoPicker(state, update, onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    checkAndRequestPermissionWithClick(
                        context, permission, launcherCamera, { shouldOpenCamera = true }
                    )
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
            val enabled =
                state.minPlayer.isNotEmpty() && state.maxPlayer.isNotEmpty() && !state.name.isNullOrEmpty() && !state.inProgress
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    addEditGame(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .size(40.dp),
                enabled = enabled
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(id = if (state.id == null) R.string.board_add else R.string.save),
                        style = SmoochBold18
                    )
                    Icon(
                        imageVector = if (state.id == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = stringResource(id = if (state.id == null) R.string.board_add else R.string.board_edit),
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(20.dp)
                    )
                }
            }

        }

        if (shouldOpenCamera && state.name != null) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                CameraView(
                    fileName = state.name,
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = { uri ->
                        Log.i(GLOBAL_TAG, "Captured image URI: $uri")
                        update(state.copy(uri = uri.toString()))
                        shouldOpenCamera = false
                    },
                    onError = {
                        Log.e(GLOBAL_TAG, "Camera view error:", it)
                    },
                    takePhoto = takePhoto,
                    backHandler = {shouldOpenCamera = false}
                )
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