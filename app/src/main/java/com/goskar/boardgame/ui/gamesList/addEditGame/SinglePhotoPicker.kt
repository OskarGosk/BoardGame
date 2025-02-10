package com.goskar.boardgame.ui.gamesList.addEditGame

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goskar.boardgame.ui.theme.SmoochBold18

@Composable
fun SinglePhotoPicker(
    state: AddEditGameState,
    update: (AddEditGameState) -> Unit = {},
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                update(state.copy(uri = uri.toString()))
            }
        }
    )

    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.Center) {
        if(state.uri == "") {
            Button(
                shape = CutCornerShape(percent = 10),
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
                    .size(40.dp),
            ) {
                Text(text = "Open Gallery",
                    style = SmoochBold18
                )
            }
        } else {
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
                Button(
                    shape = CutCornerShape(percent = 10),
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                        .size(40.dp),
                ) {
                    Text(text = "Take another photo",
                        style = SmoochBold18
                    )
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
        SinglePhotoPicker(AddEditGameState())
    }
}