package com.goskar.boardgame.ui.screens.addGame.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.screens.addGame.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameTheme
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


@Composable
fun CoverPhotoPicker(
    modifier: Modifier = Modifier,
    coverUri: String?,
    onCoverPicked: (String) -> Unit,
) {
    val context = LocalContext.current
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val file = newCoverFile(context)
            copyUriToFile(context, uri, file)
            onCoverPicked(Uri.fromFile(file).toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) pendingCameraFile?.let { onCoverPicked(Uri.fromFile(it).toString()) }
    }

    fun launchCamera() {
        val file = newCoverFile(context)
        pendingCameraFile = file
        cameraLauncher.launch(FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file))
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) launchCamera() }

    val outline = MaterialTheme.colorScheme.outlineVariant
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .drawBehind {
                    drawRoundRect(
                        color = outline,
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
                        ),
                    )
                }
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            contentAlignment = Alignment.Center,
        ) {
            if (!coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(coverUri.toUri()).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.addgame_cover_add),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppSecondaryButton(
                text = stringResource(R.string.addgame_camera),
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        launchCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f),
            )
            AppSecondaryButton(
                text = stringResource(R.string.addgame_gallery),
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val FILE_PROVIDER_AUTHORITY = "com.goskar.boardgame.fileprovider"

private fun newCoverFile(context: android.content.Context): File {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BoardGameImages").apply { mkdirs() }
    return File(dir, "${UUID.randomUUID()}.jpg")
}

private fun copyUriToFile(context: android.content.Context, source: Uri, dest: File) {
    context.contentResolver.openInputStream(source)?.use { input ->
        FileOutputStream(dest).use { output -> input.copyTo(output) }
    }
}


@Composable
private fun CoverPhotoPickerPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        CoverPhotoPicker(coverUri = null, onCoverPicked = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun CoverPhotoPickerLight() = BoardGameTheme(darkTheme = false) { CoverPhotoPickerPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CoverPhotoPickerDark() = BoardGameTheme(darkTheme = true) { CoverPhotoPickerPreviewContent() }
