package com.goskar.boardgame.data.models

import com.goskar.boardgame.utils.sha256Hex

/** Everything the app keeps in the local database and syncs to the cloud. */
data class LocalSnapshot(
    val games: List<Game>,
    val players: List<Player>,
    val history: List<HistoryGame>,
    val historyExpansions: List<HistoryGameExpansion>,
) {
    val isEmpty: Boolean
        get() = games.isEmpty() && players.isEmpty() && history.isEmpty() && historyExpansions.isEmpty()

    /**
     * Content hash that does not depend on row order. Used to tell whether the local data differs
     * from what was last uploaded or downloaded. Player.selected is UI state, so it is ignored.
     */
    fun fingerprint(): String {
        val canonical = listOf(
            games.sortedBy { it.id },
            players.sortedBy { it.id }.map { it.copy(selected = false) },
            history.sortedBy { it.id }.map { it.copy(playerScores = it.playerScores?.toSortedMap()) },
            historyExpansions.sortedBy { it.id },
        ).joinToString(separator = "\n") { it.toString() }
        return sha256Hex(canonical)
    }
}
