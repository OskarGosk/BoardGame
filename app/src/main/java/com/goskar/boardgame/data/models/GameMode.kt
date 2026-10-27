package com.goskar.boardgame.data.models

enum class GameMode { COMPETITIVE, COOPERATIVE, TEAM }

/** Winner label stored for cooperative sessions (players win together, or lose to the game). */
object CoopWinner {
    const val PLAYERS = "Players"
    const val COMPUTER = "Computer"
}

// Games saved before play modes existed (also ones synced by older app versions) only carry the co-op flag.
fun Game.playModes(): List<GameMode> =
    modes?.distinct()?.takeIf { it.isNotEmpty() }
        ?: listOf(if (cooperate) GameMode.COOPERATIVE else GameMode.COMPETITIVE)

fun HistoryGame.playMode(): GameMode = mode
    ?: if (winner == CoopWinner.PLAYERS || winner == CoopWinner.COMPUTER) GameMode.COOPERATIVE
    else GameMode.COMPETITIVE

fun HistoryGame.isWinner(playerName: String): Boolean = when (playMode()) {
    GameMode.TEAM -> winningTeam != null && teams?.get(playerName) == winningTeam
    GameMode.COOPERATIVE -> winner == CoopWinner.PLAYERS
    GameMode.COMPETITIVE -> playerName.equals(winner, ignoreCase = true)
}
