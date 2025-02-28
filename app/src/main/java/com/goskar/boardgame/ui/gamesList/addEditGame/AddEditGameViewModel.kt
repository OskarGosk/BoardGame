package com.goskar.boardgame.ui.gamesList.addEditGame

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class AddEditGameState(
    val name: String? = null,
    val expansion: Boolean = false,
    val baseGame: String? = null,
    val minPlayer: String = "",
    val maxPlayer: String = "",
    val games: Int = 0,
    val uri: String = "",
    val id: String? = null,

    val successAddEditGame: Boolean = false,
    val errorVisible: Boolean = false,
    val inProgress: Boolean = false
)

class AddEditGameViewModel(
    private val gameDbRepository: GameDbRepository,
) : ViewModel() {

    companion object {
        const val CHILD = "BoardGameImages"
        const val PNG = ".png"
    }

    private val _state = MutableStateFlow(AddEditGameState())
    val state = _state.asStateFlow()


    fun update(state: AddEditGameState) {
        _state.update { state }
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
                val inputStream = context.contentResolver.openInputStream(state.value.uri.toUri())

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
                expansion = state.value.expansion,
                baseGame = state.value.baseGame ?: "",
                minPlayer = state.value.minPlayer,
                maxPlayer = state.value.maxPlayer,
                games = state.value.games,
                uri = state.value.uri,
                id = state.value.id ?: UUID.randomUUID().toString()
            )

            val response =
                if (state.value.id.isNullOrEmpty()) gameDbRepository.insertGame(game) else gameDbRepository.editGame(
                    game
                )

            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = true,
                            inProgress = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            successAddEditGame = false,
                            errorVisible = true,
                            inProgress = false
                        )
                    }
                }
            }
        }
    }
}