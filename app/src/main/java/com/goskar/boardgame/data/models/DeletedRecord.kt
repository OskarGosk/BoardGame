package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Remembers that a game, player or session was deleted, so a sync does not bring it back from
 * another device that still has it. Session expansions follow their session and need no record.
 */
@Entity
data class DeletedRecord(
    @PrimaryKey val id: String,
    val collection: String,
    val deletedAt: Long,
) {
    companion object {
        const val GAME = "game"
        const val PLAYER = "player"
        const val HISTORY_GAME = "historyGame"
    }
}
