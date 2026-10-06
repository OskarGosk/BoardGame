package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.LocalSnapshot

class GetLocalSnapshotUseCase(
    private val getAllGame: GetAllGameUseCase,
    private val getAllPlayer: GetAllPlayerUseCase,
    private val getAllHistory: GetAllHistoryGameUseCase,
    private val getAllHistoryExpansion: GetAllHistoryGameExpansionUseCase,
) {
    suspend operator fun invoke() = LocalSnapshot(
        games = getAllGame(),
        players = getAllPlayer(),
        history = getAllHistory(),
        historyExpansions = getAllHistoryExpansion(),
    )
}
