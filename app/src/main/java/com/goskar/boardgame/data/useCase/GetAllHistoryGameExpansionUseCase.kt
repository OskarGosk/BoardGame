package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class GetAllHistoryGameExpansionUseCase  (
    private val historyDbRepository: GamesHistoryDbRepository,
) {

    suspend operator fun invoke(): List<HistoryGameExpansion> =
        when (val response = historyDbRepository.getAllHistoryGameExpansion()) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                emptyList()
            }
        }
}