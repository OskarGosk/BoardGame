package com.goskar.boardgame.ui.components.other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.SnackbarColor.onSnackbarColor
import com.goskar.boardgame.ui.theme.SnackbarColor.snackbarErrorColor
import com.goskar.boardgame.ui.theme.SnackbarColor.snackbarInfoColor
import com.goskar.boardgame.ui.theme.SnackbarColor.snackbarSuccessColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Visual variant of [AppSnackbar]. */
enum class AppSnackBarType { SUCCESS, ERROR, INFO }

/**
 * Global snackbar host. Provide it once at the app root via
 * `CompositionLocalProvider(LocalSnackbarHost provides …)` and read it on any
 * screen with `LocalSnackbarHost.current`.
 */
class AppSnackbarController(
    private val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun show(message: String, type: AppSnackBarType) {
        scope.launch { hostState.showAppSnackbar(message, type) }
    }
}

val LocalSnackbarHost = staticCompositionLocalOf<AppSnackbarController> {
    error("No AppSnackbarController provided")
}

private const val SNACKBAR_DURATION_MILLIS = 3000L
private val SnackbarCornerRadius = 16.dp
private val SnackbarIconSize = 24.dp
private val SnackbarTouchTarget = 48.dp
private val SnackbarStartPadding = 16.dp
private val SnackbarVerticalPadding = 12.dp
private val SnackbarOuterPadding = 12.dp
private val SnackbarItemSpacing = 12.dp

/** Carries the [AppSnackBarType] through the Material [SnackbarHostState]. */
class AppSnackbarVisuals(
    override val message: String,
    val type: AppSnackBarType,
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false

    override val duration: SnackbarDuration = SnackbarDuration.Indefinite
}

/** Show a styled app snackbar of the given [type]. */
suspend fun SnackbarHostState.showAppSnackbar(message: String, type: AppSnackBarType) {
    showSnackbar(AppSnackbarVisuals(message = message, type = type))
}

@Composable
fun AppSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData,
) {
    val type = (snackbarData.visuals as? AppSnackbarVisuals)?.type ?: AppSnackBarType.SUCCESS
    val backgroundColor = when (type) {
        AppSnackBarType.SUCCESS -> snackbarSuccessColor
        AppSnackBarType.ERROR -> snackbarErrorColor
        AppSnackBarType.INFO -> snackbarInfoColor
    }
    val icon: ImageVector = when (type) {
        AppSnackBarType.SUCCESS -> Icons.Default.Check
        AppSnackBarType.ERROR -> Icons.Default.Warning
        AppSnackBarType.INFO -> Icons.Default.Info
    }

    LaunchedEffect(snackbarData) {
        delay(SNACKBAR_DURATION_MILLIS)
        snackbarData.dismiss()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(SnackbarOuterPadding),
        shape = RoundedCornerShape(SnackbarCornerRadius),
        color = backgroundColor,
        contentColor = onSnackbarColor,
    ) {
        Row(
            modifier = Modifier.padding(
                start = SnackbarStartPadding,
                top = SnackbarVerticalPadding,
                bottom = SnackbarVerticalPadding,
                end = SnackbarVerticalPadding,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SnackbarItemSpacing),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(SnackbarIconSize),
            )
            Text(
                text = snackbarData.visuals.message,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = { snackbarData.dismiss() },
                modifier = Modifier.size(SnackbarTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.snackbar_dismiss),
                    modifier = Modifier.size(SnackbarIconSize),
                )
            }
        }
    }
}
