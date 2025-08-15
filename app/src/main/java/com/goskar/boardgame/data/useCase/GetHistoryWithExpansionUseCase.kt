package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class GetHistoryWithExpansionUseCase(
    private val historyDbRepository: GamesHistoryDbRepository,
) {

    suspend operator fun invoke(): RequestResult<List<HistoryGameWithExpansion>> {

        val responseGame = historyDbRepository.getAllHistoryGame()
        val responseExpansion = historyDbRepository.getAllHistoryGameExpansion()

        return when {
            responseGame is RequestResult.Error -> responseGame
            responseExpansion is RequestResult.Error -> responseExpansion
            responseGame is RequestResult.Success && responseExpansion is RequestResult.Success -> {
                RequestResult.Success(
                    responseGame.data.map { game ->
                        HistoryGameWithExpansion(
                            history = game,
                            expansion = responseExpansion.data.filter { it.historyGameId == game.id }
                        )
                    }
                )
            }
            else -> RequestResult.Error(Throwable("Coś poszło nie tak..."))
        }
    }
}


data class HistoryGameWithExpansion(
    val history: HistoryGame,
    val expansion: List<HistoryGameExpansion>?
)