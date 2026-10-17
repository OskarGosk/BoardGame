package com.goskar.boardgame.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.login.LoginScreen
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.profile.components.AccountSettingsCard
import com.goskar.boardgame.ui.screens.profile.components.CloudSyncCard
import com.goskar.boardgame.ui.screens.profile.components.ProfileHeaderCard
import com.goskar.boardgame.ui.screens.profile.components.RecentMedalsCard
import com.goskar.boardgame.ui.screens.profile.components.SignInToAccountCard
import com.goskar.boardgame.ui.screens.profile.components.SignOutButton
import com.goskar.boardgame.ui.screens.profile.components.SignOutPromptDialog
import com.goskar.boardgame.ui.screens.profile.components.StatRow
import com.goskar.boardgame.ui.screens.profile.viewmodel.*
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import org.koin.androidx.compose.koinViewModel

class ProfileNewScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: ProfileNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) { viewModel.load() }

        LaunchedEffect(state.signedOut) {
            if (state.signedOut) navigator?.replaceAll(LoginScreen())
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is ProfileEvent.ShowMessage ->
                        snackbarHostState.show(resources.getString(event.message), event.type)
                }
            }
        }

        ProfileNewScreenContent(
            state = state,
            onSignOut = viewModel::requestSignOut,
            onForceSync = viewModel::forceSync,
            onSyncAndSignOut = viewModel::syncAndSignOut,
            onSignOutWithoutSync = viewModel::signOutWithoutSync,
            onDismissSignOutPrompt = viewModel::dismissSignOutPrompt,
            onSignInToAccount = { navigator?.push(LoginScreen()) },
        )
    }
}

@Composable
fun ProfileNewScreenContent(
    state: ProfileNewState,
    onSetting: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onForceSync: () -> Unit = {},
    onViewAchievements: () -> Unit = {},
    onSyncAndSignOut: () -> Unit = {},
    onSignOutWithoutSync: () -> Unit = {},
    onDismissSignOutPrompt: () -> Unit = {},
    onSignInToAccount: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(-1) }

    SignOutPromptDialog(
        prompt = state.signOutPrompt,
        isGuest = state.isGuest,
        onSyncAndSignOut = onSyncAndSignOut,
        onSignOutWithoutSync = onSignOutWithoutSync,
        onDismiss = onDismissSignOutPrompt,
    )

    AppScaffold(
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        trailing = { AppAvatar(initials = state.initials, size = 36.dp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileHeaderCard(state = state)
            StatRow(state = state)
            AccountSettingsCard(state = state, onSetting = onSetting)
            SignOutButton(onSignOut = onSignOut)
            if (state.isGuest) SignInToAccountCard(onSignInToAccount = onSignInToAccount)
            CloudSyncCard(state = state, onForceSync = onForceSync)
            if (state.medals.isNotEmpty()) RecentMedalsCard(state = state, onViewAchievements = onViewAchievements)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(name = "Profile — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ProfileNewLightPreview() {
    BoardGameTheme(darkTheme = false) { ProfileNewScreenContent(state = ProfileNewState()) }
}

@Preview(name = "Profile — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ProfileNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { ProfileNewScreenContent(state = ProfileNewState()) }
}
