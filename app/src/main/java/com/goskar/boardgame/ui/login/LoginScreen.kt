package com.goskar.boardgame.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.BgListCard
import com.goskar.boardgame.ui.theme.BgPrimaryButton
import com.goskar.boardgame.ui.theme.BgSecondaryButton
import com.goskar.boardgame.ui.theme.BgTextField
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: LoginViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LoginScreenContent(
            state = state,
            updateLogin = viewModel::updateLogin,
            updatePassword = viewModel::updatePassword,
            questLogIn = viewModel::questAccount,
            logIn = viewModel::signIn
        )

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is LoginEvent.ShowMessage -> {
                        snackbarHostState.show(
                            message = context.getString(event.message),
                            type = event.type
                        )
                        navigator?.replaceAll(HomeScreen(state.login != "quest"))
                        ScreenLifecycleStore.remove(this@LoginScreen)
                    }

                    is LoginEvent.loggedInOrGuest -> {
                        navigator?.replaceAll(HomeScreen(state.login != "quest"))
                        ScreenLifecycleStore.remove(this@LoginScreen)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    updateLogin: (String) -> Unit = {},
    updatePassword: (String) -> Unit = {},
    questLogIn: () -> Unit = {},
    logIn: () -> Unit = {},
    paddingValues: PaddingValues,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BoardGameSpacing.MarginMobile),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo Section
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(BoardGameShapes.Large)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tabletop Tracker",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Manage your board game collection and session history in one premium space.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Login Card
        BgListCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                BgTextField(
                    value = state.login,
                    onValueChange = { updateLogin(it) },
                    label = "Email Address",
                    placeholder = "name@example.com",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Password",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            "Forgot?",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .clickable { /* Handle forgot password */ }
                        )
                    }
                    BgTextField(
                        value = state.password,
                        onValueChange = { updatePassword(it) },
                        placeholder = "••••••••",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(if (!passwordVisible) R.drawable.icons_visible else R.drawable.icons_invisible),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        passwordVisible = !passwordVisible
                                    }
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                BgPrimaryButton(
                    text = "Login",
                    onClick = logIn,
                    loading = state.isLoading
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = " OR CONNECT WITH ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                BgSecondaryButton(
                    text = "Continue as Guest",
                    onClick = questLogIn
                )

            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(
                text = "New collector? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Create an account",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Handle sign up */ }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    BoardGameTheme {
        LoginScreenContent(
            LoginState(login = "oskar@login.com", password = "Oskar"),
        )
    }
}

@Preview ()
@Composable
fun LoginScreenDarkPreview() {
    BoardGameTheme (darkTheme = true) {
        LoginScreenContent(
            LoginState(login = "oskar@login.com", password = "Oskar"),
        )
    }
}

