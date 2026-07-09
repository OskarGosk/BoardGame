package com.goskar.boardgame.ui.screens.addPlayer

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.login.LoginEvent
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.screens.home.HomeNewScreen
import com.goskar.boardgame.ui.screens.profile.ProfileNewScreen
import com.goskar.boardgame.ui.components.user.rememberUserInitials
import com.goskar.boardgame.ui.theme.AppAvatar
import com.goskar.boardgame.ui.theme.AppGhostButton
import com.goskar.boardgame.ui.theme.AppListCard
import com.goskar.boardgame.ui.theme.AppOptionGrid
import com.goskar.boardgame.ui.theme.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.theme.AppSectionHeader
import com.goskar.boardgame.ui.theme.AppTextField
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.SkillOption

private val skillOptions = listOf(
    SkillOption("Beginner", Icons.Default.School),
    SkillOption("Intermediate", Icons.Default.Star),
    SkillOption("Master", Icons.Default.MilitaryTech),
)

class AddPlayerNewScreen(
    private val editData: EditPlayerData? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddPlayerNewViewModel = org.koin.androidx.compose.koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

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
                            message = context.getString(event.message),
                            type = event.type
                        )
                    }
                    is PlayerEvent.SuccessAddEditPlayer -> {
                        snackbarHostState.show(
                            message = context.getString(event.message),
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

@Composable
private fun AddPlayerNewCard(
    state: AddPlayerNewState,
    onSelectAvatar: (Int) -> Unit,
    onNicknameChange: (String) -> Unit,
    onSelectSkill: (Int) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    AppListCard {
//        AppSectionHeader(title = "Choose Avatar", action = "SELECT ONE")
//        Spacer(Modifier.height(16.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
//            state.avatars.forEachIndexed { index, initials ->
//                AppAvatar(
//                    initials = initials,
//                    size = 72.dp,
//                    selected = index == state.selectedAvatar,
//                    modifier = Modifier.clickable { onSelectAvatar(index) },
//                )
//            }
//        }
//
//        Spacer(Modifier.height(24.dp))

        AppTextField(
            value = state.nickname,
            onValueChange = onNicknameChange,
            label = stringResource(R.string.addplayer_nickname_label),
            placeholder = stringResource(R.string.addplayer_nickname_hint),
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp),
                )
            },
        )

        Spacer(Modifier.height(24.dp))

        AppSectionHeader(title = stringResource(R.string.addplayer_skill_level))
        Spacer(Modifier.height(12.dp))
        AppOptionGrid(
            options = skillOptions,
            selected = state.selectedSkill,
            onSelect = onSelectSkill,
        )

        Spacer(Modifier.height(28.dp))

        AppPrimaryButton(
            text = if (state.isEditMode) stringResource(R.string.addplayer_save_changes) else stringResource(R.string.addplayer_save_new),
            onClick = onSave,
            leadingIcon = {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
        Spacer(Modifier.height(4.dp))
        AppGhostButton(text = stringResource(R.string.addplayer_discard), onClick = onDiscard)
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
