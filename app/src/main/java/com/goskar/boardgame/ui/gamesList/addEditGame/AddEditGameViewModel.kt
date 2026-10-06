package com.goskar.boardgame.ui.gamesList.addEditGame

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.annotation.StringRes
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.Constants.GLOBAL_TAG
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.Executor

sealed interface AddEditEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : AddEditEvent
    data class SuccessAddEditGame(@StringRes val message: Int, val type: AppSnackBarType) :
        AddEditEvent
}

data class AddEditGameState(
    val name: String? = null,
    val expansion: Boolean = false,
    val baseGame: String? = null,
    val baseGameId: String? = null,
    val minPlayer: String = "",
    val maxPlayer: String = "",
    val games: Int = 0,
    val uri: String = "",
    val uriFromBgg: String? = null,
    val id: String? = null,
    val cooperate: Boolean = false,
    val inProgress: Boolean = false
)

class AddEditGameViewModel(
    private val gameDbRepository: GameDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase
) : ViewModel() {

    companion object {
        const val CHILD = "BoardGameImages"
        const val PNG = ".png"
        const val JPG = ".jpg"
    }

    private val _state = MutableStateFlow(AddEditGameState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _allBaseGame = MutableStateFlow<List<Game>>(emptyList())
    val allBaseGame = _allBaseGame.asStateFlow()

    init {
        viewModelScope.launch {
            _allBaseGame.value = getAllGameUseCase.invoke().filter { !it.expansion }
        }
    }


    fun updateName(value: String?) {
        _state.update { it.copy(name = value) }
    }

    fun updateMinPlayer(value: String) {
        _state.update { it.copy(minPlayer = value) }
    }

    fun updateMaxPLayer(value: String) {
        _state.update { it.copy(maxPlayer = value) }
    }

    fun updateGameType() {
        _state.update {
            it.copy(
                cooperate = !state.value.cooperate
            )
        }
    }

    fun updateExpansion() {
        _state.update {
            it.copy(
                expansion = !state.value.expansion
            )
        }
    }

    fun updateBaseBase(name: String?, id: String?) {
        _state.update {
            it.copy(
                baseGame = name,
                baseGameId = id
            )
        }
    }

    fun updateCameraUri(value: String) {
        _state.update { it.copy(uri = value) }
    }

    fun updateDataForEditGame(editGame: Game) {
        _state.update {
            it.copy(
                name = editGame.name,
                expansion = editGame.expansion,
                cooperate = editGame.cooperate,
                baseGame = editGame.baseGame,
                baseGameId = editGame.baseGameId,
                minPlayer = editGame.minPlayer,
                maxPlayer = editGame.maxPlayer,
                games = editGame.games,
                uri = editGame.uri ?: "",
                uriFromBgg = editGame.uriFromBgg,
                id = editGame.id
            )
        }
    }

    fun validateAddEitGame(context: Context) {
        _state.update {
            it.copy(
                inProgress = true
            )
        }

        viewModelScope.launch {

            if (state.value.uri.isNotEmpty()) {
                val picturesDir =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CHILD)
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs()
                }

                val newFile = File(picturesDir, "${state.value.name}$PNG")
                val inputStream =
                    context.contentResolver.openInputStream(state.value.uri.toUri())

                inputStream?.use { input ->
                    val outputStream = FileOutputStream(newFile)
                    input.copyTo(outputStream)
                    outputStream.close()

                    val fileUri = Uri.fromFile(newFile)
                    _state.update {
                        it.copy(
                            uri = fileUri.toString()
                        )
                    }
                }
            }
            val game = Game(
                name = state.value.name ?: "",
                expansion = state.value.expansion && !state.value.baseGame.isNullOrEmpty(),
                cooperate = state.value.cooperate,
                baseGame = state.value.baseGame ?: "",
                baseGameId = state.value.baseGameId,
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                uri = state.value.uri,
                uriFromBgg = state.value.uriFromBgg,
                id = state.value.id ?: UUID.randomUUID().toString()
            )

            val response =
                if (state.value.id.isNullOrEmpty()) gameDbRepository.insertGame(game) else gameDbRepository.editGame(
                    game
                )

            when (response) {
                is RequestResult.Success -> {
                    _events.send(
                        AddEditEvent.SuccessAddEditGame(
                            R.string.success_global,
                            AppSnackBarType.SUCCESS
                        )
                    )
                    _state.update {
                        it.copy(
                            inProgress = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(
                        AddEditEvent.ShowMessage(
                            R.string.error_global,
                            AppSnackBarType.ERROR
                        )
                    )
                    _state.update {
                        it.copy(
                            inProgress = false
                        )
                    }
                }
            }
        }
    }

    fun takePhoto(
        fileName: String,
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onImageCaptured: (Uri) -> Unit = {},
        onError: (ImageCaptureException) -> Unit = {}
    ) {

        val photoFile = File(
            outputDirectory,
            fileName + JPG
        )

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Timber.tag(GLOBAL_TAG).e(exception, "Take Photo error:")
                    onError(exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri)
                }
            })
    }
}