package com.goskar.boardgame.ui.screens.addGame
import com.goskar.boardgame.R
import androidx.compose.ui.res.stringResource
import com.goskar.boardgame.ui.screens.addGame.viewmodel.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.components.other.LocalSnackbarHost
import com.goskar.boardgame.ui.screens.gameDetails.GameDetailsNewScreen
import com.goskar.boardgame.ui.navigation.appNavItems
import com.goskar.boardgame.ui.components.AppChip
import com.goskar.boardgame.ui.components.AppChipStyle
import com.goskar.boardgame.ui.components.AppListCard
import com.goskar.boardgame.ui.components.AppPoweredByBgg
import com.goskar.boardgame.ui.components.AppPrimaryButton
import com.goskar.boardgame.ui.theme.AppScaffold
import com.goskar.boardgame.ui.components.AppSearchBar
import com.goskar.boardgame.ui.components.AppSecondaryButton
import com.goskar.boardgame.ui.components.AppSectionHeader
import com.goskar.boardgame.ui.components.AppSegmentedControl
import com.goskar.boardgame.ui.components.AppTextField
import com.goskar.boardgame.ui.components.AppToggleRow
import com.goskar.boardgame.ui.theme.BoardGameShapes
import com.goskar.boardgame.ui.theme.BoardGameSpacing
import com.goskar.boardgame.ui.theme.BoardGameTheme
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import org.koin.androidx.compose.koinViewModel

class AddGameNewScreen(
    private val editGameId: String? = null,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddGameNewViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current
        val snackbarHostState = LocalSnackbarHost.current
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if (editGameId != null) viewModel.loadForEdit(editGameId)
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddGameEvent.Saved -> {
                        snackbarHostState.show(context.getString(event.message), event.type)
                        navigator?.pop()
                    }

                    is AddGameEvent.ShowMessage ->
                        snackbarHostState.show(context.getString(event.message), event.type)
                }
            }
        }

        AddGameNewScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onQueryChange = viewModel::updateQuery,
            onSearch = viewModel::search,
            onSelectTab = viewModel::selectTab,
            onAddGame = viewModel::addFromBgg,
            onOpenDetails = { match -> navigator?.push(GameDetailsNewScreen(match.id, match.name)) },
            onNameChange = viewModel::updateName,
            onMinChange = viewModel::updateMinPlayers,
            onMaxChange = viewModel::updateMaxPlayers,
            onToggleCooperate = viewModel::toggleCooperate,
            onToggleExpansion = viewModel::toggleExpansion,
            onSelectBaseGame = viewModel::selectBaseGame,
            onCategoryChange = viewModel::updateCategory,
            onYearChange = viewModel::updateYear,
            onCoverPicked = viewModel::updateCoverUri,
            onSaveManual = viewModel::saveManual,
        )
    }
}

@Composable
fun AddGameNewScreenContent(
    state: AddGameNewState,
    onBack: () -> Unit = {},
    onHelp: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    onAddGame: (PopularMatch) -> Unit = {},
    onOpenDetails: (PopularMatch) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onMinChange: (String) -> Unit = {},
    onMaxChange: (String) -> Unit = {},
    onToggleCooperate: (Boolean) -> Unit = {},
    onToggleExpansion: (Boolean) -> Unit = {},
    onSelectBaseGame: (String) -> Unit = {},
    onCategoryChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    onCoverPicked: (String) -> Unit = {},
    onSaveManual: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(1) }

    AppScaffold(
        title = if (state.isEditMode) stringResource(R.string.addgame_title_edit)
        else stringResource(R.string.addgame_title),
        navItems = appNavItems,
        selectedTab = selectedNav,
        onTabSelected = { selectedNav = it },
        onBack = onBack,
        trailing = {
            IconButton(onClick = onHelp) {
                Icon(Icons.Default.HelpOutline, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BoardGameSpacing.MarginMobile),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            if (!state.isEditMode) {
                AppSegmentedControl(
                    options = listOf(stringResource(R.string.addgame_tab_bgg), stringResource(R.string.addgame_tab_manual)),
                    selected = state.selectedTab,
                    onSelect = onSelectTab,
                )
            }

            if (state.selectedTab == 0 && !state.isEditMode) {
                AppSearchBar(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.addgame_search_hint),
                    onSearch = onSearch,
                )
                AppSectionHeader(title = stringResource(R.string.addgame_popular_matches))
                state.popularMatches.forEach { match ->
                    PopularMatchCard(
                        match = match,
                        onAdd = { onAddGame(match) },
                        onOpen = { onOpenDetails(match) },
                    )
                }
                AppPoweredByBgg()
            } else {
                ManualEntryForm(
                    state = state,
                    onNameChange = onNameChange,
                    onMinChange = onMinChange,
                    onMaxChange = onMaxChange,
                    onToggleCooperate = onToggleCooperate,
                    onToggleExpansion = onToggleExpansion,
                    onSelectBaseGame = onSelectBaseGame,
                    onCategoryChange = onCategoryChange,
                    onYearChange = onYearChange,
                    onCoverPicked = onCoverPicked,
                    onSave = onSaveManual,
                )
            }
        }
    }
}

@Composable
private fun ManualEntryForm(
    state: AddGameNewState,
    onNameChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    onToggleCooperate: (Boolean) -> Unit,
    onToggleExpansion: (Boolean) -> Unit,
    onSelectBaseGame: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCoverPicked: (String) -> Unit,
    onSave: () -> Unit,
) {
    CoverPhotoPicker(coverUri = state.coverUri, onCoverPicked = onCoverPicked)

    AppTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = stringResource(R.string.addgame_name_label),
        placeholder = stringResource(R.string.addgame_name_hint),
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTextField(
            value = state.minPlayers,
            onValueChange = onMinChange,
            label = stringResource(R.string.addgame_min_players),
            placeholder = "1",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
        AppTextField(
            value = state.maxPlayers,
            onValueChange = onMaxChange,
            label = stringResource(R.string.addgame_max_players),
            placeholder = "4",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTextField(
            value = state.category,
            onValueChange = onCategoryChange,
            label = stringResource(R.string.addgame_category_label),
            placeholder = "Strategy",
            modifier = Modifier.weight(1f),
        )
        AppTextField(
            value = state.year,
            onValueChange = onYearChange,
            label = stringResource(R.string.addgame_year_label),
            placeholder = "2019",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
    }

    AppToggleRow(
        icon = Icons.Default.Groups,
        title = stringResource(R.string.addgame_coop_title),
        description = stringResource(R.string.addgame_coop_desc),
        checked = state.cooperate,
        onToggle = onToggleCooperate,
    )
    AppToggleRow(
        icon = Icons.Default.Extension,
        title = stringResource(R.string.addgame_exp_title),
        description = stringResource(R.string.addgame_exp_desc),
        checked = state.expansion,
        onToggle = onToggleExpansion,
    )

    if (state.expansion) {
        ManualSelectField(
            label = stringResource(R.string.board_base),
            value = state.baseGame,
            placeholder = stringResource(R.string.addgame_base_hint),
            options = state.baseGameOptions,
            onSelect = onSelectBaseGame,
        )
    }

    Spacer(Modifier.height(4.dp))
    AppPrimaryButton(
        text = if (state.isEditMode) stringResource(R.string.addgame_update) else stringResource(R.string.addgame_save),
        onClick = onSave,
        enabled = state.name.isNotBlank(),
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CoverPhotoPicker(coverUri: String?, onCoverPicked: (String) -> Unit) {
    val context = LocalContext.current
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val file = newCoverFile(context)
            copyUriToFile(context, uri, file)
            onCoverPicked(Uri.fromFile(file).toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) pendingCameraFile?.let { onCoverPicked(Uri.fromFile(it).toString()) }
    }

    fun launchCamera() {
        val file = newCoverFile(context)
        pendingCameraFile = file
        cameraLauncher.launch(FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file))
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) launchCamera() }

    val outline = MaterialTheme.colorScheme.outlineVariant
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .drawBehind {
                    drawRoundRect(
                        color = outline,
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
                        ),
                    )
                }
                .clip(BoardGameShapes.ExtraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            contentAlignment = Alignment.Center,
        ) {
            if (!coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(coverUri.toUri()).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.addgame_cover_add),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppSecondaryButton(
                text = stringResource(R.string.addgame_camera),
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        launchCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f),
            )
            AppSecondaryButton(
                text = stringResource(R.string.addgame_gallery),
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val FILE_PROVIDER_AUTHORITY = "com.goskar.boardgame.fileprovider"

private fun newCoverFile(context: android.content.Context): File {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BoardGameImages").apply { mkdirs() }
    return File(dir, "${UUID.randomUUID()}.jpg")
}

private fun copyUriToFile(context: android.content.Context, source: Uri, dest: File) {
    context.contentResolver.openInputStream(source)?.use { input ->
        FileOutputStream(dest).use { output -> input.copyTo(output) }
    }
}

@Composable
private fun ManualSelectField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, BoardGameShapes.Medium)
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularMatchCard(match: PopularMatch, onAdd: () -> Unit, onOpen: () -> Unit = {}) {
    AppListCard(onClick = onOpen) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(BoardGameShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(match.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppChip(match.category, AppChipStyle.CATEGORY)
                    AppChip(match.year, AppChipStyle.YEAR)
                }
            }
            IconButton(onClick = onAdd) {
                Icon(
                    Icons.Default.AddCircleOutline,
                    contentDescription = "Add ${match.name}",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Preview(name = "Add Game — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun AddGameNewLightPreview() {
    BoardGameTheme(darkTheme = false) { AddGameNewScreenContent(state = AddGameNewState()) }
}

@Preview(name = "Add Game — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun AddGameNewDarkPreview() {
    BoardGameTheme(darkTheme = true) { AddGameNewScreenContent(state = AddGameNewState()) }
}
