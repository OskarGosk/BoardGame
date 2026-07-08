package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.utils.convertHistoryGameListToFirebase

/**
 * Pushes the full local database (games, players, history, history expansions) to Firebase.
 * Runs the four uploads sequentially and stops on the first failure.
 */
class UploadToCloudUseCase(
    private val api: BoardGameFirebaseDataRepository,
    private val getAllGame: GetAllGameUseCase,
    private val getAllPlayer: GetAllPlayerUseCase,
    private val getAllHistory: GetAllHistoryGameUseCase,
    private val getAllHistoryExpansion: GetAllHistoryGameExpansionUseCase,
) {
    suspend operator fun invoke(): Boolean {
        val games = getAllGame().associateBy { it.id }
        if (api.addAllGame(games) !is RequestResult.Success) return false

        val players = getAllPlayer().associateBy { it.id }
        if (api.addPlayer(players) !is RequestResult.Success) return false

        val history = convertHistoryGameListToFirebase(getAllHistory()).associateBy { it.id }
        if (api.addHistoryGame(history) !is RequestResult.Success) return false

        val expansions = getAllHistoryExpansion().associateBy { it.id }
        if (api.addHistoryGameExpansion(expansions) !is RequestResult.Success) return false

        return true
    }
}
