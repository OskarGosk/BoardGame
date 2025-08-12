package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class UpsertAllHistoryGameExpansionUseCase(
    private val historyDbRepository: GamesHistoryDbRepository
) {

    suspend operator fun invoke(allHistoryExpansion: List<HistoryGameExpansion>): Boolean =
        when (val response = historyDbRepository.insertAllHistoryGameExpansion(allHistoryExpansion)) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                false
            }
        }

}