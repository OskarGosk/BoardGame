package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class GetAllHistoryGameUseCase (
    private val historyDbRepository: GamesHistoryDbRepository,
) {

    suspend operator fun invoke(): List<HistoryGame> =
        when (val response = historyDbRepository.getAllHistoryGame()) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                emptyList()
            }
        }
}