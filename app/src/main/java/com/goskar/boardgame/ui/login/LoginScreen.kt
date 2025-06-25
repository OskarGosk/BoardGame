package com.goskar.boardgame.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppLoader
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.Smooch16
import com.goskar.boardgame.ui.theme.Smooch20
import com.goskar.boardgame.utils.Keyboard
import com.goskar.boardgame.utils.keyboardAsState
import org.koin.androidx.compose.koinViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: LoginViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        LaunchedEffect(state.successLogin) {
            if (state.successLogin) {
                navigator?.replaceAll(HomeScreen(state.login != "quest"))
                ScreenLifecycleStore.remove(this@LoginScreen)

            }
        }
        LaunchedEffect(key1 = state.isLoggedIn) {
            viewModel.checkIfLoggedIn()
            if (state.isLoggedIn) {
                navigator?.replaceAll(HomeScreen(false))
                ScreenLifecycleStore.remove(this@LoginScreen)

            }
        }
        BoardGameScaffold(
            titlePage = stringResource(R.string.login_screen),
            selectedScreen = null,
            showBottomBar = false,
            showSynchronizedIcon = false
        ) { paddingValues ->
            LoginContent(
                state = state,
                update = viewModel::update,
                logIn = viewModel::signIn,
                questLogIn = viewModel::questAccount,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun LoginContent(
    state: LoginState,
    update: (LoginState) -> Unit = {},
    logIn: () -> Unit = {},
    questLogIn: () -> Unit = {},
    paddingValues: PaddingValues
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()

    if (state.isLoading) AppLoader()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        verticalArrangement = if (isKeyboardOpen == Keyboard.Opened) Arrangement.Top else Arrangement.Center
    ) {
        if (isKeyboardOpen == Keyboard.Opened) Spacer(modifier = Modifier.height(100.dp))
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
                if (state.login.isNotEmpty() && state.password.isNotEmpty()) logIn()
            }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        painter = painterResource(if (!passwordVisible) R.drawable.icons_visible else R.drawable.icons_invisible),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = {
                Text(
                    text = stringResource(R.string.password),
                    style = Smooch16
                )
            },
            textStyle = Smooch20
        )

        Row {
            Button(
                shape = CutCornerShape(percent = 10),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                enabled = (state.login.isNotEmpty() && state.password.isNotEmpty()),
                onClick = {
                    logIn()
                }) {
                Text(
                    stringResource(R.string.login)
                )
            }

            Spacer(
                modifier = Modifier.width(15.dp)
            )
            if (state.password.isEmpty()) {
                OutlinedButton(
                    shape = CutCornerShape(percent = 10),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    onClick = {
                        questLogIn()
                    }) {
                    Text(
                        stringResource(R.string.guest_account)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun LoginContentPreview() {
    BoardGameScaffold(
        titlePage = stringResource(R.string.login_screen),
        selectedScreen = null,
        showBottomBar = false,
        showSynchronizedIcon = false
    ) { paddingValues ->
        LoginContent(
            LoginState(login = "Oskar@login.pl", password = "Oskar"),
            paddingValues = paddingValues
        )
    }
}
