package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.utils.convertHistoryGameListToFirebase

class UploadToCloudUseCase(
    private val api: BoardGameFirebaseDataRepository,
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val syncState: SyncStateRepository,
) {
    suspend operator fun invoke(): Boolean {
        val snapshot = getSnapshot()

        if (api.addAllGame(snapshot.games.associateBy { it.id }) !is RequestResult.Success) return false
        if (api.addPlayer(snapshot.players.associateBy { it.id }) !is RequestResult.Success) return false

        val history = convertHistoryGameListToFirebase(snapshot.history).associateBy { it.id }
        if (api.addHistoryGame(history) !is RequestResult.Success) return false

        val expansions = snapshot.historyExpansions.associateBy { it.id }
        if (api.addHistoryGameExpansion(expansions) !is RequestResult.Success) return false

        syncState.markSynced(snapshot.fingerprint())
        return true
    }
}
