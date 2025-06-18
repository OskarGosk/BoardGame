package com.goskar.boardgame.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameFirebase
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun checkAndRequestPermissionWithClick(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onClick: () -> Unit = {}
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Do on click
        onClick()
    } else {
        // Request a permission
        launcher.launch(permission)
    }
}


suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun convertHistoryGameListToDto(oldList :List<HistoryGameFirebase>): List<HistoryGame> {
    return oldList.map { historyGame ->
        HistoryGame(
            gameName = historyGame.gameName,
            winner = historyGame.winner,
            gameData = LocalDate.parse(historyGame.gameData), // LocalDate -> String
            listOfPlayer = historyGame.listOfPlayer,
            description = historyGame.description,
            id = historyGame.id
        )
    }
}

fun convertHistoryGameListToFirebase(oldList: List<HistoryGame>): List<HistoryGameFirebase> {
    return oldList.map { historyGame ->
        HistoryGameFirebase(
            gameName = historyGame.gameName,
            winner = historyGame.winner,
            gameData = historyGame.gameData.toString(), // LocalDate -> String
            listOfPlayer = historyGame.listOfPlayer,
            description = historyGame.description,
            id = historyGame.id
        )
    }
}
