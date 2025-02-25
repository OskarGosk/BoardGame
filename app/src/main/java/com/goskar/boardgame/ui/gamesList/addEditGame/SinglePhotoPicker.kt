package com.goskar.boardgame.ui.gamesList.addEditGame

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.goskar.boardgame.ui.theme.primaryLight


@Composable
fun SinglePhotoPicker(
    state: AddEditGameState,
    update: (AddEditGameState) -> Unit = {},
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val permission = Manifest.permission.CAMERA
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                update(state.copy(uri = uri.toString()))
            }
        }
    )

    val cameraPermission = ContextCompat.checkSelfPermission(
        context, permission
    )

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (state.uri == "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = {
                        launcher.launch("image/*")
                    },
                ) {
                    Text(
                        text = stringResource(R.string.board_open_gallery),
                        style = SmoochBold18
                    )
                }
                if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                    Button(
                        shape = CutCornerShape(percent = 10),
                        onClick = {
                            onClick()
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.board_open_camera),
                            style = SmoochBold18
                        )
                    }
                }
            }

        } else {

            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = primaryLight,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        update(
                            state.copy(
                                uri = ""
                            )
                        )
                    }
            )
            Column {
                val imageUri = Uri.parse(state.uri)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                        .padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        shape = CutCornerShape(percent = 10),
                        onClick = {
                            launcher.launch("image/*")
                        },
                        enabled = state.name != null,
                        modifier = Modifier
                    ) {
                        Text(
                            text = stringResource(R.string.board_open_gallery),
                            style = SmoochBold18
                        )
                    }
                    if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                        Button(
                            shape = CutCornerShape(percent = 10),
                            enabled = state.name != null,
                            onClick = {
                                onClick()
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.board_open_camera),
                                style = SmoochBold18
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun SinglePhotoPickerPreview() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SinglePhotoPicker(AddEditGameState(uri = "oskar"))
    }
}

@Preview
@Composable
fun SinglePhotoPicker2Preview() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SinglePhotoPicker(
            AddEditGameState(uri = "", name = "Oskar"),
        )
    }
}