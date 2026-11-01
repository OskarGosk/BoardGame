package com.goskar.boardgame.data.models

/** Everything stored under users/{uid}. Firebase drops empty maps, so every collection may be missing. */
data class CloudUserData(
    val game: Map<String, Game>? = null,
    val player: Map<String, Player>? = null,
    val historyGame: Map<String, HistoryGameFirebase>? = null,
    val historyGameExpansion: Map<String, HistoryGameExpansion>? = null,
    val deleted: Map<String, DeletedRecord>? = null,
) {
    val isEmpty: Boolean
        get() = game.isNullOrEmpty() && player.isNullOrEmpty() &&
            historyGame.isNullOrEmpty() && historyGameExpansion.isNullOrEmpty() && deleted.isNullOrEmpty()
}
