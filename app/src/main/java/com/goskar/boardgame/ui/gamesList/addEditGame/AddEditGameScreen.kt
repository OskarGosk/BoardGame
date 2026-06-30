package com.goskar.boardgame.ui.gamesList.addEditGame

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.components.other.CameraView
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import org.koin.androidx.compose.koinViewModel
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.bottomBar.BottomBarElements
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gamesList.addEditGame.components.DropdownBaseGame
import com.goskar.boardgame.ui.gamesList.lists.GameListScreen
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.utils.checkAndRequestPermissionWithClick
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddEditGameScreen(private val editGame: Game?) : Screen {

    @Composable
    override fun Content() {

        val viewModel: AddEditGameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val allBaseGame by viewModel.allBaseGame.collectAsState()

        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()

        val navigator = LocalNavigator.current

        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddEditEvent.ShowMessage -> {
                        snackbarHostState.show(
                            message = context.getString(event.message),
                            type = event.type
                        )
                    }

                    is AddEditEvent.SuccessAddEditGame -> {
                        snackbarHostState.show(
                            message = context.getString(event.message),
                            type = event.type
                        )
                        navigator?.replace(GameListScreen())
                    }
                }
            }
        }

        LaunchedEffect(editGame) {
            if (editGame != null) {
                viewModel.updateDataForEditGame(editGame)
            }
        }
        BoardGameScaffold(
            titlePage = stringResource(if (state.name == null) R.string.board_new else R.string.board_edit),
            selectedScreen = BottomBarElements.GameListButton.title,
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase
        ) { paddingValues ->
            AddEditGameContent(
                state = state,
                allBaseGame = allBaseGame,
                addEditGame = viewModel::validateAddEitGame,
                takePhoto = viewModel::takePhoto,
                updateName = viewModel::updateName,
                updateMinPlayer = viewModel::updateMinPlayer,
                updateMaxPLayer = viewModel::updateMaxPLayer,
                updateGameType = viewModel::updateGameType,
                updateExpansion = viewModel::updateExpansion,
                updateBaseBase = viewModel::updateBaseBase,
                updateCameraUri = viewModel::updateCameraUri,
                paddingValues = paddingValues
            )
        }
    }
}


@Composable
fun AddEditGameContent(
    state: AddEditGameState,
    allBaseGame: List<Game>,
    addEditGame: (Context) -> Unit = {},
    takePhoto: (String, ImageCapture, File, Executor, (Uri) -> Unit, (ImageCaptureException) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    updateName: (String?) -> Unit = {},
    updateMinPlayer: (String) -> Unit = {},
    updateMaxPLayer: (String) -> Unit = {},
    updateGameType: () -> Unit = {},
    updateExpansion: () -> Unit = {},
    updateBaseBase: (String?, String?) -> Unit = { _, _ -> },
    updateCameraUri: (String) -> Unit = {},
    paddingValues: PaddingValues

) {

    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHost.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var shouldOpenCamera by remember { mutableStateOf(false) }
    val permission = Manifest.permission.CAMERA

    val outputDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldOpenCamera = true
        } else {
            snackbarHostState.show(
                message = context.getString(R.string.camera_denied),
                type = AppSnackBarType.INFO
            )
        }
    }


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
                updateName(it)
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
                    updateMinPlayer(it)
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
                    updateMaxPLayer(it)
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
                    updateGameType()

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
                    updateExpansion()
                },
            )
            Text(stringResource(id = R.string.board_is_expansion), style = Smooch18)
        }
        if (state.expansion) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DropdownBaseGame(allBaseGame, selectedName = state.baseGame, selectBaseGame = {

                    updateBaseBase(it?.name, it?.id)
                })
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            SinglePhotoPicker(state, updateCameraUri = updateCameraUri, onClick = {
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
                    Timber.tag(GLOBAL_TAG).i("Captured image URI: $uri")
                    updateCameraUri(uri.toString())
                    shouldOpenCamera = false
                },
                onError = {
                    Timber.tag(GLOBAL_TAG).e(it, "Camera view error:")
                },
                takePhoto = takePhoto,
                backHandler = { shouldOpenCamera = false }
            )
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

    BoardGameScaffold(
        titlePage = stringResource(if (state.name == null) R.string.board_new else R.string.board_edit),
        selectedScreen = BottomBarElements.GameListButton.title
    ) { paddingValues ->
        AddEditGameContent(state, allBaseGame = emptyList(), paddingValues = paddingValues)
    }
}