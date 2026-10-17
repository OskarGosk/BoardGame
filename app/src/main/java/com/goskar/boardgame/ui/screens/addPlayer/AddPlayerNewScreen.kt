package com.goskar.boardgame.ui.screens.addPlayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.navigation.AppScaffold
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.addPlayer.components.AddPlayerNewCard
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme

class AddPlayerNewScreen(
    private val editData: EditPlayerData? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddPlayerNewViewModel = org.koin.androidx.compose.koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val resources = LocalResources.current

        LaunchedEffect(Unit) {
            if (editData != null && !state.isEditMode) viewModel.loadForEdit(editData)
        }

        AddPlayerNewScreenContent(
            state = state,
            userInitials = rememberUserInitials(),
            onBack = { navigator?.pop() },
            onSelectAvatar = viewModel::selectAvatar,
            onNicknameChange = viewModel::updateNickname,
            onSelectSkill = viewModel::selectSkill,
            onSave = viewModel::validateAddEditPLayer,
            onDiscard = { navigator?.pop() },
            onProfileClick = { navigator?.push(ProfileNewScreen()) },
        )

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is PlayerEvent.ShowMessage -> {
                        snackbarHostState.show(
                            message = resources.getString(event.message),
                            type = event.type
                        )
                    }
                    is PlayerEvent.SuccessAddEditPlayer -> {
                        snackbarHostState.show(
                            message = resources.getString(event.message),
                            type = event.type
                        )
                        navigator?.pop()
                    }

                }
            }
        }
    }
}

@Composable
fun AddPlayerNewScreenContent(
    state: AddPlayerNewState,
    userInitials: String = "AM",
    onBack: () -> Unit = {},
    onSelectAvatar: (Int) -> Unit = {},
    onNicknameChange: (String) -> Unit = {},
    onSelectSkill: (Int) -> Unit = {},
    onSave: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(3) }

    AppScaffold(
        title = if (state.isEditMode) stringResource(R.string.addplayer_title_edit) else stringResource(R.string.addplayer_title_new),
        navItems = appNavItems,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onBack = onBack,
        trailing = {
            AppAvatar(
                initials = userInitials,
                size = 36.dp,
                modifier = Modifier.clip(CircleShape).clickable { onProfileClick() },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(BoardGameSpacing.MarginMobile),
        ) {
            AddPlayerNewCard(
                state = state,
                onSelectAvatar = onSelectAvatar,
                onNicknameChange = onNicknameChange,
                onSelectSkill = onSelectSkill,
                onSave = onSave,
                onDiscard = onDiscard,
            )
        }
    }
}

@Preview(name = "Add Player — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddPlayerNewScreenLightPreview() {
    BoardGameTheme(darkTheme = false) { AddPlayerNewScreenContent(state = AddPlayerNewState()) }
}

@Preview(name = "Add Player — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddPlayerNewScreenDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddPlayerNewScreenContent(state = AddPlayerNewState()) }
}
