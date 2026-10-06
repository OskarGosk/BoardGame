package com.goskar.boardgame.data.models

data class GuestBackupMeta(
    val updatedAt: Long = 0L,
    val gameCount: Int = 0,
    val playerCount: Int = 0,
    val sessionCount: Int = 0,
)

/** Wire format of a guest backup. Firebase drops empty maps, so every collection may be missing. */
data class GuestBackupDto(
    val meta: GuestBackupMeta = GuestBackupMeta(),
    val game: Map<String, Game>? = null,
    val player: Map<String, Player>? = null,
    val historyGame: Map<String, HistoryGameFirebase>? = null,
    val historyGameExpansion: Map<String, HistoryGameExpansion>? = null,
)
