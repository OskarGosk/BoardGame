package com.goskar.boardgame.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.Smooch20
import org.koin.androidx.compose.koinViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: LoginViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        LaunchedEffect(state.successLogin) {
            if(state.successLogin) {
                navigator?.push(HomeScreen())
            }
        }
        LaunchedEffect(key1 = state.isLoggedIn) {
            viewModel.checkIfLoggedIn()
            if (state.isLoggedIn) {
                navigator?.replaceAll(HomeScreen())
            }
        }
        LoginContent(
            state = state,
            update = viewModel::update,
            logIn = viewModel::signIn)
    }
}

@Composable
fun LoginContent(
    state: LoginState,
    update: (LoginState) -> Unit = {},
    logIn: () -> Unit = {}
) {

    val passwordVisible by remember { mutableStateOf(false) }

    BoardGameScaffold(
        titlePage = stringResource(R.string.login_screen),
        selectedScreen = null,
        showBottomBar = false,
        showSynchronizedIcon = false
    ) { paddingValues ->
        if(state.isLoading) AppLoader()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                value = state.login,
                onValueChange = {
                    update(
                        state.copy(
                            login = it
                        )
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                ),
                label = {
                    Text(
                        text = stringResource(R.string.login),
                        style = Smooch16
                    )
                },
                textStyle = Smooch20
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp),
                value = state.password,
                onValueChange = {
                    update(
                        state.copy(
                            password = it
                        )
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if(state.login.isNotEmpty() && state.password.isNotEmpty()) logIn()
                }),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                label = {
                    Text(
                        text = stringResource(R.string.password),
                        style = Smooch16
                    )
                },
                textStyle = Smooch20
            )

            Button(
                shape = CutCornerShape(percent = 10),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                enabled = (state.login.isNotEmpty() && state.password.isNotEmpty()),
                onClick = {
                    logIn()
                }) {
                Text(
                    stringResource(R.string.login)
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginContentPreview() {
    LoginContent(
        LoginState(login = "Oskar@login.pl", password = "Oskar")
    )
}
