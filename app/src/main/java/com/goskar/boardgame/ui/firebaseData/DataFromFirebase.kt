package com.goskar.boardgame.ui.firebaseData

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.login.LoginViewModel
import com.goskar.boardgame.ui.theme.Smooch14
import com.goskar.boardgame.ui.theme.Smooch18
import org.koin.androidx.compose.koinViewModel

class DataFromFirebase : Screen {
    @Composable
    override fun Content() {
        val viewModel: DataFromFirebaseViewModel = koinViewModel()
        val viewModel2: LoginViewModel = koinViewModel()
        DataFromFirebaseContent(
            uploadBase= viewModel::uploadDataToFirebase,
            downloadBase = viewModel::downloadDataFromFirebase,
            logOut = viewModel2::signOut
        )
    }
}

@Composable
fun DataFromFirebaseContent(
    uploadBase: () -> Unit = {},
    downloadBase: () -> Unit = {},
    logOut: () -> Unit = {}
) {

    BoardGameScaffold(
        titlePage = "Data Firebase",
        selectedScreen = null
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "KOD DOSTĘPU")

            OutlinedTextField(
                textStyle = Smooch18,
                shape = RoundedCornerShape(15),
                value = "",
                onValueChange = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                label = {
                    Text(
                        text = "Oskar",
                        style = Smooch14
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                }),
            )

            Button(
                onClick = {
                    logOut()
                }
            ) {
                Text(text = "LogOut")
            }

            Button(
                onClick = {
                    uploadBase()
                }
            ) {
                Text(text = "Upload")
            }

            Button(
                onClick = {
                    downloadBase()
                }
            ) {
                Text(text = "Downlad")
            }
        }
    }
}


@Preview
@Composable
fun DataFromFirebasePreview() {
    DataFromFirebaseContent()
}