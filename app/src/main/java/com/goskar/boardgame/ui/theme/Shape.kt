package com.goskar.boardgame.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// =============================================================================
// SHAPES — BoardGame
// -----------------------------------------------------------------------------
// Jeden obiekt — oba motywy używają identycznego języka kształtów.
// Signature radius: 24dp (ExtraLarge) dla kart i modali.
// =============================================================================

object BoardGameShapes {
    val ExtraSmall = RoundedCornerShape(4.dp)   // status chips (dark)
    val Small      = RoundedCornerShape(8.dp)   // ikony, tagi
    val Medium     = RoundedCornerShape(12.dp)  // przyciski, inputy, kafelki
    val Large      = RoundedCornerShape(16.dp)  // małe karty, list rows
    val ExtraLarge = RoundedCornerShape(24.dp)  // game cards, modale, hero cards
    val Full       = CircleShape                // avatary, FAB, pille, search bar
}

// =============================================================================
// SPACING — BoardGame
// =============================================================================

object BoardGameSpacing {
    // Micro
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp

    // Standard
    val Base = 16.dp
    val Lg   = 24.dp
    val Xl   = 40.dp
    val Xxl  = 64.dp

    // Layout
    val MarginMobile   = 16.dp
    val MarginDesktop  = 40.dp
    val Gutter         = 24.dp
    val SectionGap     = 40.dp

    // Component-specific
    val CardPadding     = 16.dp
    val InputHeight     = 56.dp
    val ButtonHeight    = 56.dp
    val TopBarHeight    = 60.dp
    val BottomBarHeight = 68.dp
}

// =============================================================================
// ELEVATION — BoardGame
// -----------------------------------------------------------------------------
// Light: klasyczne cienie (dp).
// Dark:  tonal layering — efekt przez kolor powierzchni, nie cień.
// =============================================================================

object BoardGameElevation {
    val None   = 0.dp
    val Low    = 1.dp   // dark cards (tonal)
    val Level1 = 1.dp
    val Medium = 2.dp   // light cards
    val High   = 6.dp   // light modals, FAB
}

object BoardGameDarkSpacing {
    val Xs = BoardGameSpacing.Xs
    val Sm = BoardGameSpacing.Sm
    val Md = BoardGameSpacing.Md
    val Base = BoardGameSpacing.Base
    val Lg = BoardGameSpacing.Lg
    val Xl = BoardGameSpacing.Xl
    val MarginMobile = BoardGameSpacing.MarginMobile
}

object BoardGameDarkElevation {
    val None = BoardGameElevation.None
    val Level1 = BoardGameElevation.Level1
    val Low = BoardGameElevation.Low
    val Medium = BoardGameElevation.Medium
    val High = BoardGameElevation.High
}
