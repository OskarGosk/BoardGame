package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =============================================================================
// COLOR SCHEMES
// =============================================================================

private val BoardGameLightColorScheme = lightColorScheme(
    primary               = BoardGameColors.Primary,
    onPrimary             = BoardGameColors.OnPrimary,
    primaryContainer      = BoardGameColors.PrimaryContainer,
    onPrimaryContainer    = BoardGameColors.OnPrimaryContainer,
    inversePrimary        = BoardGameColors.InversePrimary,
    secondary             = BoardGameColors.Secondary,
    onSecondary           = BoardGameColors.OnSecondary,
    secondaryContainer    = BoardGameColors.SecondaryContainer,
    onSecondaryContainer  = BoardGameColors.OnSecondaryContainer,
    tertiary              = BoardGameColors.Tertiary,
    onTertiary            = BoardGameColors.OnTertiary,
    tertiaryContainer     = BoardGameColors.TertiaryContainer,
    onTertiaryContainer   = BoardGameColors.OnTertiaryContainer,
    background            = BoardGameColors.Background,
    onBackground          = BoardGameColors.OnSurface,
    surface               = BoardGameColors.Surface,
    onSurface             = BoardGameColors.OnSurface,
    onSurfaceVariant      = BoardGameColors.OnSurfaceVariant,
    inverseSurface        = BoardGameColors.InverseSurface,
    inverseOnSurface      = BoardGameColors.InverseOnSurface,
    outline               = BoardGameColors.Outline,
    outlineVariant        = BoardGameColors.OutlineVariant,
    error                 = BoardGameColors.Error,
    onError               = BoardGameColors.OnError,
    errorContainer        = BoardGameColors.ErrorContainer,
    onErrorContainer      = BoardGameColors.OnErrorContainer,
    surfaceContainer      = BoardGameColors.SurfaceContainer,
    surfaceContainerHigh  = BoardGameColors.SurfaceContainerHigh,
    surfaceContainerLow   = BoardGameColors.SurfaceContainerLow,
    surfaceContainerLowest = BoardGameColors.SurfaceContainerLowest,
    surfaceContainerHighest = BoardGameColors.SurfaceContainerHighest,
    scrim                 = BoardGameColors.Scrim,
)

private val BoardGameDarkColorScheme = darkColorScheme(
    primary               = BoardGameDarkColors.Primary,
    onPrimary             = BoardGameDarkColors.OnPrimary,
    primaryContainer      = BoardGameDarkColors.PrimaryContainer,
    onPrimaryContainer    = BoardGameDarkColors.OnPrimaryContainer,
    inversePrimary        = BoardGameDarkColors.InversePrimary,
    secondary             = BoardGameDarkColors.Secondary,
    onSecondary           = BoardGameDarkColors.OnSecondary,
    secondaryContainer    = BoardGameDarkColors.SecondaryContainer,
    onSecondaryContainer  = BoardGameDarkColors.OnSecondaryContainer,
    tertiary              = BoardGameDarkColors.Tertiary,
    onTertiary            = BoardGameDarkColors.OnTertiary,
    tertiaryContainer     = BoardGameDarkColors.TertiaryContainer,
    onTertiaryContainer   = BoardGameDarkColors.OnTertiaryContainer,
    background            = BoardGameDarkColors.Background,
    onBackground          = BoardGameDarkColors.OnSurface,
    surface               = BoardGameDarkColors.Surface,
    onSurface             = BoardGameDarkColors.OnSurface,
    onSurfaceVariant      = BoardGameDarkColors.OnSurfaceVariant,
    inverseSurface        = BoardGameDarkColors.InverseSurface,
    inverseOnSurface      = BoardGameDarkColors.InverseOnSurface,
    outline               = BoardGameDarkColors.Outline,
    outlineVariant        = BoardGameDarkColors.OutlineVariant,
    error                 = BoardGameDarkColors.Error,
    onError               = BoardGameDarkColors.OnError,
    errorContainer        = BoardGameDarkColors.ErrorContainer,
    onErrorContainer      = BoardGameDarkColors.OnErrorContainer,
    surfaceContainer      = BoardGameDarkColors.SurfaceContainer,
    surfaceContainerHigh  = BoardGameDarkColors.SurfaceContainerHigh,
    surfaceContainerLow   = BoardGameDarkColors.SurfaceContainerLow,
    surfaceContainerLowest = BoardGameDarkColors.SurfaceContainerLowest,
    surfaceContainerHighest = BoardGameDarkColors.SurfaceContainerHighest,
    surfaceVariant        = BoardGameDarkColors.SurfaceContainerHighest,
    scrim                 = BoardGameDarkColors.Scrim,
)

// =============================================================================
// TYPOGRAPHY SCHEMES
// =============================================================================

private val BoardGameLightTypographyScheme = Typography(
    displayLarge   = BoardGameTypography.DisplayLg,
    displayMedium  = BoardGameTypography.DisplayLgMobile,
    headlineMedium = BoardGameTypography.HeadlineMd,
    titleLarge     = BoardGameTypography.TitleLg,
    bodyLarge      = BoardGameTypography.BodyLg,
    bodyMedium     = BoardGameTypography.BodySm,
    labelSmall     = BoardGameTypography.LabelCaps,
    labelMedium    = BoardGameTypography.LabelCaps,
)

private val BoardGameDarkTypographyScheme = Typography(
    displayLarge   = BoardGameTypography.DisplayLg,
    displayMedium  = BoardGameTypography.DisplayLgMobileDark,
    headlineMedium = BoardGameTypography.HeadlineMd,
    titleLarge     = BoardGameTypography.TitleLg,
    bodyLarge      = BoardGameTypography.BodyLgDark,
    bodyMedium     = BoardGameTypography.BodyMdDark,
    labelSmall     = BoardGameTypography.LabelLg,
    labelMedium    = BoardGameTypography.LabelMd,
)

// =============================================================================
// MAIN THEME — BoardGame
// -----------------------------------------------------------------------------
// Jeden wrapper dla obu motywów.
// Automatycznie przełącza się na podstawie ustawień systemu.
//
// Użycie w MainActivity:
//   BoardGameTheme { AppScaffold(navController) }
//
// Wymuszenie motywu (np. w Preview):
//   BoardGameTheme(darkTheme = true) { ... }
// =============================================================================

@Composable
fun BoardGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) BoardGameDarkColorScheme else BoardGameLightColorScheme,
        typography  = if (darkTheme) BoardGameDarkTypographyScheme  else BoardGameLightTypographyScheme,
        content     = content
    )
}

@Composable
fun BoardGameDarkTheme(content: @Composable () -> Unit) = BoardGameTheme(darkTheme = true, content = content)

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Theme — Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun ThemeLightPreview() {
    BoardGameTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Board Game — Light",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(name = "Theme — Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun ThemeDarkPreview() {
    BoardGameTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Board Game — Dark",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}