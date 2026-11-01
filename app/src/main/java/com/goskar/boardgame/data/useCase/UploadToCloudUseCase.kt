package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.CloudUserData
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

        val data = CloudUserData(
            game = snapshot.games.associateBy { it.id },
            player = snapshot.players.associateBy { it.id },
            historyGame = convertHistoryGameListToFirebase(snapshot.history).associateBy { it.id },
            historyGameExpansion = snapshot.historyExpansions.associateBy { it.id },
        )
        if (api.uploadAll(data) !is RequestResult.Success) return false

        syncState.markSynced(snapshot.fingerprint())
        return true
    }
}
