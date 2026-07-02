package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.newDesign.BgDarkProgressBar
import com.goskar.boardgame.ui.newDesign.BgDarkSectionHeader
import com.goskar.boardgame.ui.newDesign.BgProgressBar
import com.goskar.boardgame.ui.newDesign.BgSectionHeader

// =============================================================================
// THEME-AWARE FACADES
// -----------------------------------------------------------------------------
// One `App*` composable per design-system component that exists as a light/dark
// pair (`Bg*` / `BgDark*`). Each facade picks the right variant based on the
// system theme, so screens compose with a single API and adapt automatically.
//
// Colors/typography used directly in screens should come from `MaterialTheme.*`
// (BoardGameTheme maps both palettes into MaterialTheme). For non-Material tokens
// (Success, CardBorder, …) use [AppExtColors] via [appExt].
// =============================================================================

@Composable
fun appIsDark(): Boolean = isSystemInDarkTheme()

// -----------------------------------------------------------------------------
// Chips
// -----------------------------------------------------------------------------

enum class AppChipStyle { CATEGORY, STATUS_WIN, STATUS_PLACE, YEAR, EXPANSION, BASE_GAME }

private fun AppChipStyle.toLight(): BgChipStyle = when (this) {
    AppChipStyle.CATEGORY -> BgChipStyle.CATEGORY
    AppChipStyle.STATUS_WIN -> BgChipStyle.STATUS_WIN
    AppChipStyle.STATUS_PLACE -> BgChipStyle.STATUS_PLACE
    AppChipStyle.YEAR -> BgChipStyle.YEAR
    AppChipStyle.EXPANSION -> BgChipStyle.EXPANSION
    AppChipStyle.BASE_GAME -> BgChipStyle.BASE_GAME
}

private fun AppChipStyle.toDark(): BgDarkChipStyle = when (this) {
    AppChipStyle.CATEGORY -> BgDarkChipStyle.CATEGORY
    AppChipStyle.STATUS_WIN -> BgDarkChipStyle.STATUS_WIN
    AppChipStyle.STATUS_PLACE -> BgDarkChipStyle.STATUS_PLACE
    AppChipStyle.YEAR -> BgDarkChipStyle.CATEGORY // dark has no YEAR style
    AppChipStyle.EXPANSION -> BgDarkChipStyle.EXPANSION
    AppChipStyle.BASE_GAME -> BgDarkChipStyle.BASE_GAME
}

@Composable
fun AppChip(
    text: String,
    style: AppChipStyle = AppChipStyle.CATEGORY,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    if (appIsDark()) BgDarkChip(text, style.toDark(), modifier, onClick)
    else BgChip(text, style.toLight(), modifier, onClick)
}

// -----------------------------------------------------------------------------
// Navigation
// -----------------------------------------------------------------------------

@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    if (appIsDark()) {
        BgDarkTopBar(title = title, modifier = modifier, onBack = onBack, trailingContent = trailing)
    } else {
        BgTopBar(title = title, modifier = modifier, onBack = onBack, trailingIcon = trailing)
    }
}

@Composable
fun AppBottomNavBar(
    items: List<BgNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (appIsDark()) BgDarkBottomNavBar(items, selected, onSelect, modifier)
    else BgBottomNavBar(items, selected, onSelect, modifier)
}

// -----------------------------------------------------------------------------
// Cards
// -----------------------------------------------------------------------------

@Composable
fun AppListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (appIsDark()) BgDarkListCard(modifier = modifier, onClick = onClick, content = content)
    else BgListCard(modifier = modifier, onClick = onClick, content = content)
}

/**
 * Stat card. In light the [icon] sits on the trailing edge, in dark it leads — matching each
 * variant's native layout.
 */
@Composable
fun AppStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    icon: @Composable (() -> Unit)? = null,
) {
    if (appIsDark()) {
        BgDarkStatCard(label = label, value = value, modifier = modifier, valueColor = valueColor, leadingIcon = icon)
    } else {
        BgStatCard(label = label, value = value, modifier = modifier, valueColor = valueColor, trailingContent = icon)
    }
}

// -----------------------------------------------------------------------------
// Section header
// -----------------------------------------------------------------------------

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    if (appIsDark()) BgDarkSectionHeader(title = title, modifier = modifier, action = action, onAction = onAction)
    else BgSectionHeader(title = title, modifier = modifier, action = action, onAction = onAction)
}

// -----------------------------------------------------------------------------
// Avatar
// -----------------------------------------------------------------------------

@Composable
fun AppAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    initials: String? = null,
    selected: Boolean = false,
    onlineStatus: Boolean? = null,
    imageContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    if (appIsDark()) BgDarkAvatar(modifier, size, initials, selected, onlineStatus, imageContent)
    else BgAvatar(modifier, size, initials, selected, onlineStatus, imageContent)
}

// -----------------------------------------------------------------------------
// FAB
// -----------------------------------------------------------------------------

@Composable
fun AppFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    if (appIsDark()) BgDarkFab(onClick, modifier, icon) else BgFab(onClick, modifier, icon)
}

// -----------------------------------------------------------------------------
// Progress bar
// -----------------------------------------------------------------------------

@Composable
fun AppProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    if (appIsDark()) BgDarkProgressBar(progress = progress, modifier = modifier)
    else BgProgressBar(progress = progress, modifier = modifier)
}
