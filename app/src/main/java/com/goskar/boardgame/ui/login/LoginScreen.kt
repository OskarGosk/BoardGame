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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.screens.home.HomeScreen
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.login.components.LocalDataDialog
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
        val resources = LocalResources.current

        LoginScreenContent(
            state = state,
            updateLogin = viewModel::updateLogin,
            updatePassword = viewModel::updatePassword,
            questLogIn = viewModel::questAccount,
            logIn = viewModel::signIn,
            onMergeLocalData = viewModel::mergeLocalDataIntoAccount,
            onDiscardLocalData = viewModel::discardLocalData,
            onCancelLocalData = viewModel::cancelSignIn,
        )

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is LoginEvent.ShowMessage -> {
                        snackbarHostState.show(
                            message = resources.getString(event.message),
                            type = event.type
                        )
                        navigator?.replaceAll(HomeScreen(firstLogin = true, mergeLocalData = event.mergeLocalData))
                        ScreenLifecycleStore.remove(this@LoginScreen)
                    }

                    is LoginEvent.LoggedInOrGuest -> {
                        navigator?.replaceAll(HomeScreen(firstLogin = false))
                        ScreenLifecycleStore.remove(this@LoginScreen)
                    }

                    is LoginEvent.ShowErrorMessage -> {
                        snackbarHostState.show(
                            message = event.message ?: resources.getString(R.string.error_global),
                            type = AppSnackBarType.ERROR
                        )
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
    onMergeLocalData: () -> Unit = {},
    onDiscardLocalData: () -> Unit = {},
    onCancelLocalData: () -> Unit = {},
) {
    var passwordVisible by remember { mutableStateOf(false) }

    state.pendingLocalData?.let { summary ->
        LocalDataDialog(
            summary = summary,
            onMerge = onMergeLocalData,
            onDiscard = onDiscardLocalData,
            onCancel = onCancelLocalData,
        )
    }

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
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Login Card
        AppListCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AppTextField(
                    value = state.login,
                    onValueChange = { updateLogin(it) },
                    label = stringResource(R.string.login_email_label),
                    placeholder = stringResource(R.string.login_email_hint),
                    isError = state.loginError,
                    supportingText = if (state.loginError) stringResource(R.string.field_required) else null,
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
                            stringResource(R.string.login_password_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            stringResource(R.string.login_forgot_password),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clickable { /* Handle forgot password */ }
                        )
                    }
                    AppTextField(
                        value = state.password,
                        onValueChange = { updatePassword(it) },
                        placeholder = "••••••••",
                        isError = state.passwordError,
                        supportingText = if (state.passwordError) stringResource(R.string.field_required) else null,
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
                AppPrimaryButton(
                    text = stringResource(R.string.login_action),
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
                            text = stringResource(R.string.login_or_connect_with),
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

                AppSecondaryButton(
                    text = stringResource(R.string.login_continue_as_guest),
                    onClick = questLogIn
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = stringResource(R.string.login_new_collector),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.login_create_account),
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
private fun LoginScreenPreview() {
    BoardGameTheme {
        LoginScreenContent(
            LoginState(login = "oskar@login.com", password = "Oskar"),
        )
    }
}

@Preview()
@Composable
private fun LoginScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        LoginScreenContent(
            LoginState(login = "oskar@login.com", password = "Oskar"),
        )
    }
}

@Preview
@Composable
private fun LoginScreenErrorPreview() {
    BoardGameTheme {
        LoginScreenContent(
            LoginState(loginError = true, passwordError = true),
        )
    }
}

