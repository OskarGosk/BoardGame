package com.goskar.boardgame.ui.components.other

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.goskar.boardgame.utils.getCameraProvider
import java.io.File
import java.util.concurrent.Executor

@OptIn(InternalVoyagerApi::class)
@Composable
fun CameraView(
    fileName: String,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit = {},
    onError: (ImageCaptureException) -> Unit = {},
    takePhoto: (String, ImageCapture, File, Executor, (Uri) -> Unit, (ImageCaptureException) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    backHandler: () -> Unit = {}
) {
    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    BackHandler(enabled = true) {
        backHandler()
    }

    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    // 3
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            { previewView },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                takePhoto(
                    fileName,
                    imageCapture,
                    outputDirectory,
                    executor,
                    onImageCaptured,
                    onError
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}