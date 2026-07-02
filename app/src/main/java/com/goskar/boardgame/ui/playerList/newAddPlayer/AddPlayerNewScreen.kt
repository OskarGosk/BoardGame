package com.goskar.boardgame.ui.playerList.newAddPlayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.newDesign.BgDarkSectionHeader
import com.goskar.boardgame.ui.theme.BgDarkOptionGrid
import com.goskar.boardgame.ui.theme.SkillOption
import com.goskar.boardgame.ui.theme.BgDarkAvatar
import com.goskar.boardgame.ui.theme.BgDarkBottomNavBar
import com.goskar.boardgame.ui.theme.BgDarkGhostButton
import com.goskar.boardgame.ui.theme.BgDarkListCard
import com.goskar.boardgame.ui.theme.BgDarkPrimaryButton
import com.goskar.boardgame.ui.theme.BgDarkTextField
import com.goskar.boardgame.ui.theme.BgDarkTopBar
import com.goskar.boardgame.ui.theme.BgNavItem
import com.goskar.boardgame.ui.theme.BoardGameDarkColors
import com.goskar.boardgame.ui.theme.BoardGameDarkSpacing
import com.goskar.boardgame.ui.theme.BoardGameDarkTheme

private val skillOptions = listOf(
    SkillOption("Beginner", Icons.Default.School),
    SkillOption("Intermediate", Icons.Default.Star),
    SkillOption("Master", Icons.Default.MilitaryTech),
)

private val darkNavItems = listOf(
    BgNavItem("Home", Icons.Default.Home),
    BgNavItem("Collection", Icons.AutoMirrored.Filled.List),
    BgNavItem("Add Session", Icons.Default.Add),
    BgNavItem("Players", Icons.Default.Person),
)

/**
 * New "Add Player" screen (dark redesign). Backed by [AddPlayerNewViewModel] (form state only).
 *
 * Built as a full destination because the mock carries its own top bar (back arrow) AND the
 * bottom nav bar — chrome that a modal sheet wouldn't own. The form body lives in
 * [AddPlayerNewCard] so it can be dropped into a ModalBottomSheet later if we switch approach.
 */
class AddPlayerNewScreen(
    private val editData: EditPlayerData? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddPlayerNewViewModel = org.koin.androidx.compose.koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            if (editData != null && !state.isEditMode) viewModel.loadForEdit(editData)
        }

        AddPlayerNewScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onSelectAvatar = viewModel::selectAvatar,
            onNicknameChange = viewModel::updateNickname,
            onSelectSkill = viewModel::selectSkill,
            onDiscard = { navigator?.pop() },
        )
    }
}

@Composable
fun AddPlayerNewScreenContent(
    state: AddPlayerNewState,
    onBack: () -> Unit = {},
    onSelectAvatar: (Int) -> Unit = {},
    onNicknameChange: (String) -> Unit = {},
    onSelectSkill: (Int) -> Unit = {},
    onSave: () -> Unit = {},
    onDiscard: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(3) } // Players tab

    BoardGameDarkTheme {
        Scaffold(
            containerColor = BoardGameDarkColors.Background,
            topBar = {
                BgDarkTopBar(
                    title = if (state.isEditMode) "Edit Player" else "Add New Player",
                    onBack = onBack,
                    trailingContent = { BgDarkAvatar(initials = "AM", size = 36.dp) },
                )
            },
            bottomBar = {
                BgDarkBottomNavBar(darkNavItems, selectedTab, { selectedTab = it })
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(BoardGameDarkSpacing.MarginMobile),
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
}

/**
 * The form card body — reusable in a full screen or a modal bottom sheet.
 */
@Composable
private fun AddPlayerNewCard(
    state: AddPlayerNewState,
    onSelectAvatar: (Int) -> Unit,
    onNicknameChange: (String) -> Unit,
    onSelectSkill: (Int) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    BgDarkListCard {
        // Choose Avatar
        BgDarkSectionHeader(title = "Choose Avatar", action = "SELECT ONE")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            state.avatars.forEachIndexed { index, initials ->
                BgDarkAvatar(
                    initials = initials,
                    size = 72.dp,
                    selected = index == state.selectedAvatar,
                    modifier = Modifier.clickable { onSelectAvatar(index) },
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Nickname
        BgDarkTextField(
            value = state.nickname,
            onValueChange = onNicknameChange,
            label = "Player Nickname",
            placeholder = "Enter name…",
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = BoardGameDarkColors.Outline,
                    modifier = Modifier.size(20.dp),
                )
            },
        )

        Spacer(Modifier.height(24.dp))

        // Skill level
        BgDarkSectionHeader(title = "Skill Level")
        Spacer(Modifier.height(12.dp))
        BgDarkOptionGrid(
            options = skillOptions,
            selected = state.selectedSkill,
            onSelect = onSelectSkill,
        )

        Spacer(Modifier.height(28.dp))

        BgDarkPrimaryButton(
            text = if (state.isEditMode) "Save Changes" else "Save Player",
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
        BgDarkGhostButton(text = "Discard Changes", onClick = onDiscard)
    }
}

@Preview(name = "Add Player — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddPlayerNewScreenPreview() {
    AddPlayerNewScreenContent(state = AddPlayerNewState(nickname = ""))
}
