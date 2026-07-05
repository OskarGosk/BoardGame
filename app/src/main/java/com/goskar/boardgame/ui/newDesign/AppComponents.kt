package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.navigation.BgNavItem
import com.goskar.boardgame.ui.newDesign.BgProgressBar
import com.goskar.boardgame.ui.newDesign.BgSectionHeader


enum class AppChipStyle { CATEGORY, STATUS_WIN, STATUS_PLACE, YEAR, EXPANSION, BASE_GAME }

@Composable
fun appExt(): BoardGameExtColors = LocalBoardGameExtColors.current


@Composable
fun AppChip(text: String, style: AppChipStyle = AppChipStyle.CATEGORY, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) =
    BgChip(text, style, modifier, onClick)

@Composable
fun AppTopBar(title: String, modifier: Modifier = Modifier, onBack: (() -> Unit)? = null, trailing: @Composable (() -> Unit)? = null) =
    BgTopBar(title = title, modifier = modifier, onBack = onBack, trailingIcon = trailing)

@Composable
fun AppBottomNavBar(items: List<BgNavItem>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) =
    BgBottomNavBar(items, selected, onSelect, modifier)

@Composable
fun AppListCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) =
    BgListCard(modifier = modifier, onClick = onClick, content = content)

@Composable
fun AppStatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = MaterialTheme.colorScheme.primary, icon: @Composable (() -> Unit)? = null) =
    BgStatCard(label = label, value = value, modifier = modifier, valueColor = valueColor, trailingContent = icon)

@Composable
fun AppSectionHeader(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: (() -> Unit)? = null) =
    BgSectionHeader(title = title, modifier = modifier, action = action, onAction = onAction)

@Composable
fun AppAvatar(modifier: Modifier = Modifier, size: Dp = 48.dp, initials: String? = null, selected: Boolean = false, onlineStatus: Boolean? = null, imageContent: @Composable (BoxScope.() -> Unit)? = null) =
    BgAvatar(modifier, size, initials, selected, onlineStatus, imageContent)

@Composable
fun AppFab(onClick: () -> Unit, modifier: Modifier = Modifier, icon: @Composable () -> Unit) =
    BgFab(onClick, modifier, icon)

@Composable
fun AppProgressBar(progress: Float, modifier: Modifier = Modifier) =
    BgProgressBar(progress = progress, modifier = modifier)

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
) = BgTextField(value, onValueChange, modifier, placeholder, label, leadingIcon, trailingIcon,
    keyboardOptions, visualTransformation, singleLine, isError, supportingText)

@Composable
fun AppSearchBar(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, placeholder: String = "Search…", trailingIcon: @Composable (() -> Unit)? = null) =
    BgSearchBar(value, onValueChange, modifier, placeholder, trailingIcon)

@Composable
fun AppTextArea(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, placeholder: String = "", label: String? = null, minLines: Int = 4) =
    BgTextArea(value, onValueChange, modifier, placeholder, label, minLines)

@Composable
fun AppSegmentedControl(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) =
    BgSegmentedControl(options, selected, onSelect, modifier)

@Composable
fun AppOptionGrid(options: List<SkillOption>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) =
    BgSkillSelector(options, selected, onSelect, modifier)

@Composable
fun AppVariantChip(text: String, selected: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) =
    BgVariantChip(text, selected, onToggle, modifier)

@Composable
fun AppFilterChip(text: String, selected: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) =
    BgFilterChip(text, selected, onToggle, modifier)

@Composable
fun AppPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, loading: Boolean = false, leadingIcon: @Composable (() -> Unit)? = null) =
    BgPrimaryButton(text, onClick, modifier, enabled, loading, leadingIcon)

@Composable
fun AppSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    BgSecondaryButton(text, onClick, modifier, enabled)

@Composable
fun AppGhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) =
    BgGhostButton(text, onClick, modifier)

@Composable
fun AppToggleRow(icon: ImageVector, title: String, checked: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier, description: String? = null) =
    BgToggleRow(icon, title, checked, onToggle, modifier, description)

@Composable
fun AppSettingsRow(icon: ImageVector, title: String, modifier: Modifier = Modifier, subtitle: String? = null, trailing: @Composable (() -> Unit)? = null, onClick: (() -> Unit)? = null) =
    BgSettingsRow(icon = icon, title = title, modifier = modifier, subtitle = subtitle, trailingContent = trailing, onClick = onClick)

@Composable
fun AppHeroCard(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeStyle: AppChipStyle = AppChipStyle.BASE_GAME,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    imageContent: @Composable BoxScope.() -> Unit,
) = BgHeroCard(title = title, modifier = modifier, badge = badge, badgeStyle = badgeStyle, subtitle = subtitle, onClick = onClick, imageContent = imageContent)


@Composable
fun AppDropdownField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select…",
) {
    Column(modifier = modifier) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(BoardGameShapes.Medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, BoardGameShapes.Medium)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = value.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
            )
            Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AppPlayerRow(
    initials: String,
    name: String,
    role: String,
    winRate: String,
    rank: String,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false,
    winRateColor: Color = MaterialTheme.colorScheme.tertiary,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    AppListCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppAvatar(size = 48.dp, initials = initials, onlineStatus = isOnline)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                Text(role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("WIN RATE", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(winRate, style = MaterialTheme.typography.titleLarge, color = winRateColor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("RANK", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MilitaryTech, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(rank, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            if (trailing != null) trailing()
        }
    }
}
