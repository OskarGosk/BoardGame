package com.goskar.boardgame.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// COLOR TOKENS — BoardGame
// =============================================================================

// -----------------------------------------------------------------------------
// RAW PALETTE — surowe wartości, nie używaj bezpośrednio w komponentach
// -----------------------------------------------------------------------------

private object Palette {
    // Orange / Terracotta
    val Orange20  = Color(0xFF502600)
    val Orange30  = Color(0xFF4F2500)
    val Orange40  = Color(0xFF713700)
    val Orange50  = Color(0xFF944A00)
    val Orange60  = Color(0xFF9E3D00)
    val Orange70  = Color(0xFFC64F00)
    val Orange80  = Color(0xFFE67E22)
    val Orange90  = Color(0xFFFFB783)
    val Orange95  = Color(0xFFFFB595)

    // Brown / Secondary
    val Brown30  = Color(0xFF472A06)
    val Brown50  = Color(0xFF64421D)
    val Brown60  = Color(0xFF745853)
    val Brown70  = Color(0xFFDFAF81)
    val Brown80  = Color(0xFFEEBD8E)
    val Brown90  = Color(0xFFFED7D0)

    // Green / Success
    val Green10  = Color(0xFF00210C)
    val Green20  = Color(0xFF003919)
    val Green40  = Color(0xFF3B6D11)
    val Green60  = Color(0xFF00B05C)
    val Green80  = Color(0xFF4AE183)
    val GreenBg  = Color(0xFFEAF3DE)

    // Surface — light
    val White    = Color(0xFFFFFFFF)
    val Linen    = Color(0xFFFAF7F2)
    val Blue99   = Color(0xFFF7F9FF)
    val Blue95   = Color(0xFFEDF4FF)
    val Blue90   = Color(0xFFE3EFFF)
    val Blue85   = Color(0xFFD9EAFF)
    val Blue80   = Color(0xFFD1E4FB)
    val Blue70   = Color(0xFFC9DCF3)
    val Navy10   = Color(0xFF091D2E)
    val Navy20   = Color(0xFF203243)
    val Navy30   = Color(0xFF313030)

    // Surface — dark
    val Dark00   = Color(0xFF0E0E0E)
    val Dark05   = Color(0xFF131313)
    val Dark10   = Color(0xFF1A1A1A)
    val Dark12   = Color(0xFF1C1B1B)
    val Dark15   = Color(0xFF201F1F)
    val Dark20   = Color(0xFF2A2A2A)
    val Dark25   = Color(0xFF353534)
    val Dark30   = Color(0xFF393939)
    val Dark35   = Color(0xFF333333)

    // Warm neutrals
    val Warm30   = Color(0xFF564337)
    val Warm50   = Color(0xFF594238)
    val Warm60   = Color(0xFF8C7166)
    val Warm70   = Color(0xFFA48C7D)
    val Warm80   = Color(0xFFDCC1B1)
    val Warm90   = Color(0xFFE0C0B2)
    val Warm95   = Color(0xFFE5E2E1)

    // Error
    val Red10    = Color(0xFF690005)
    val Red20    = Color(0xFF93000A)
    val Red80    = Color(0xFFFFB4AB)
    val Red90    = Color(0xFFFFDAD6)
    val RedLight = Color(0xFFBA1A1A)

    // Nav dark
    val NavDarkChip = Color(0xFF3D2B10)
}

// -----------------------------------------------------------------------------
// LIGHT COLORS
// -----------------------------------------------------------------------------

object BoardGameColors {
    val Primary             = Palette.Orange60
    val OnPrimary           = Palette.White
    val PrimaryContainer    = Palette.Orange70
    val OnPrimaryContainer  = Palette.White
    val InversePrimary      = Palette.Orange95

    val Secondary            = Palette.Brown60
    val OnSecondary          = Palette.White
    val SecondaryContainer   = Palette.Brown90
    val OnSecondaryContainer = Color(0xFF795C57)

    val Tertiary            = Color(0xFF655A4C)
    val OnTertiary          = Palette.White
    val TertiaryContainer   = Color(0xFF7E7363)
    val OnTertiaryContainer = Palette.White

    val Background              = Palette.Blue99
    val Surface                 = Palette.Blue99
    val SurfaceDim              = Palette.Blue70
    val SurfaceBright           = Palette.Blue99
    val SurfaceContainerLowest  = Palette.White
    val SurfaceContainerLow     = Palette.Blue95
    val SurfaceContainer        = Palette.Blue90
    val SurfaceContainerHigh    = Palette.Blue85
    val SurfaceContainerHighest = Palette.Blue80

    val OnSurface        = Palette.Navy10
    val OnSurfaceVariant = Palette.Warm50
    val InverseSurface   = Palette.Navy20
    val InverseOnSurface = Color(0xFFE8F2FF)

    val Outline        = Palette.Warm60
    val OutlineVariant = Palette.Warm90

    val Error            = Palette.RedLight
    val OnError          = Palette.White
    val ErrorContainer   = Palette.Red90
    val OnErrorContainer = Palette.Red20

    val Success   = Palette.Green40
    val SuccessBg = Palette.GreenBg

    val PrimaryTint10   = Palette.Orange60.copy(alpha = 0.10f)
    val PrimaryTint20   = Palette.Orange60.copy(alpha = 0.20f)
    val SecondaryTint10 = Palette.Brown60.copy(alpha = 0.10f)

    val Divider       = Color(0x1A000000)
    val Scrim         = Color(0x52000000)
    val NavBackground = Palette.Linen
    val NavActiveDot  = Primary
    val CardBorder    = OutlineVariant.copy(alpha = 0.5f)
}

// -----------------------------------------------------------------------------
// DARK COLORS
// -----------------------------------------------------------------------------

object BoardGameDarkColors {
    val Primary             = Palette.Orange90
    val OnPrimary           = Palette.Orange30
    val PrimaryContainer    = Palette.Orange80
    val OnPrimaryContainer  = Palette.Orange20
    val InversePrimary      = Palette.Orange50

    val Secondary            = Palette.Brown80
    val OnSecondary          = Palette.Brown30
    val SecondaryContainer   = Palette.Brown50
    val OnSecondaryContainer = Palette.Brown70

    val Tertiary            = Palette.Green80
    val OnTertiary          = Palette.Green20
    val TertiaryContainer   = Palette.Green60
    val OnTertiaryContainer = Palette.Green10

    val Background              = Palette.Dark05
    val Surface                 = Palette.Dark05
    val SurfaceContainerLowest  = Palette.Dark00
    val SurfaceContainerLow     = Palette.Dark12
    val SurfaceContainer        = Palette.Dark15
    val SurfaceContainerHigh    = Palette.Dark20
    val SurfaceContainerHighest = Palette.Dark25
    val SurfaceBright           = Palette.Dark30
    val SurfaceDim              = Palette.Dark05

    val OnSurface        = Palette.Warm95
    val OnSurfaceVariant = Palette.Warm80
    val InverseSurface   = Palette.Warm95
    val InverseOnSurface = Palette.Navy30

    val Outline        = Palette.Warm70
    val OutlineVariant = Palette.Warm30
    val SurfaceTint    = Palette.Orange90

    val Error            = Palette.Red80
    val OnError          = Palette.Red10
    val ErrorContainer   = Palette.Red20
    val OnErrorContainer = Palette.Red90

    val Success   = Palette.Green80
    val SuccessBg = Palette.Green20

    val PrimaryTint10 = Palette.Orange90.copy(alpha = 0.10f)
    val PrimaryTint15 = Palette.Orange90.copy(alpha = 0.15f)
    val PrimaryTint20 = Palette.Orange90.copy(alpha = 0.20f)
    val OrangeFill    = Palette.Orange80

    val NavBackground = Palette.Dark12
    val NavActiveChip = Palette.NavDarkChip
    val NavActiveTint = Palette.Orange90

    val Divider    = Palette.Dark20
    val Scrim      = Color(0xCC000000)
    val CardBorder = Palette.Dark35
    val InputBg    = Palette.Dark10
}

object SnackbarColor {
    // Snackbar status colors (success = green, error = red) + on-color for content.
    val snackbarSuccessColor = Color(0xFF2E7D32)
    val snackbarErrorColor = Color(0xFFC62828)
    val snackbarInfoColor = Color(0xFF534438)

    val onSnackbarColor = Color(0xFFFFFFFF)
}