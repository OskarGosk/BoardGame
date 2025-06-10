package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class UpsertAllHistoryGameUseCase(
    private val historyDbRepository: GamesHistoryDbRepository
) {

    suspend operator fun invoke(allHistory: List<HistoryGame>): Boolean =
        when (val response = historyDbRepository.insertAllHistoryGame(allHistory)) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                false
            }
        }

}