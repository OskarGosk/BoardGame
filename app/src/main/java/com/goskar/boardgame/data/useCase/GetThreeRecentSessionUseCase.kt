package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.utils.coverUri


class GetThreeRecentSessionUseCase(
    private val getAllGameUseCase: GetAllGameUseCase,
    private val getAllHistoryGameUseCase: GetAllHistoryGameUseCase,
) {

    suspend operator fun invoke(): List<RecentSession> {

        val allGames = getAllGameUseCase.invoke()
        val allHistory = getAllHistoryGameUseCase.invoke()

        val recent = allHistory.sortedByDescending { it.gameData }.take(3).map { historyGame ->
            // HistoryGame.baseGameId points at the parent of an expansion (null for base games), so match by name.
            val game = allGames.firstOrNull { it.name == historyGame.gameName }

            RecentSession(
                gameName = historyGame.gameName,
                date = historyGame.gameData.year.toString(),
                playersInitials = historyGame.listOfPlayer.map { initialsOf(it) },
                winner = "Winner: ${historyGame.winner}",
                uri = game?.coverUri() ?: ""
            )
        }

        return recent
    }
}

data class RecentSession(
    val gameName: String,
    val date: String,
    val playersInitials: List<String>,
    val winner: String,
    val uri: String = "",
)

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+"))
        .mapNotNull { it.firstOrNull() }
        .take(2)
        .joinToString("")
        .ifBlank { name.take(2) }
        .uppercase()

