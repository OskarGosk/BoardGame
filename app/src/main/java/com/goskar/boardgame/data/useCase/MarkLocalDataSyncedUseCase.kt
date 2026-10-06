package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.syncState.SyncStateRepository

/** Records the current local data as synced, e.g. right after it was downloaded from the cloud. */
class MarkLocalDataSyncedUseCase(
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val syncState: SyncStateRepository,
) {
    suspend operator fun invoke() = syncState.markSynced(getSnapshot().fingerprint())
}
