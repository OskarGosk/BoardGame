package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.syncState.SyncStateRepository

data class SyncStatus(
    val hasUnsyncedChanges: Boolean,
    val lastSyncedAt: Long?,
)

/** Compares the local data with what was last synced. Empty local data never counts as unsynced. */
class GetSyncStatusUseCase(
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val syncState: SyncStateRepository,
) {
    suspend operator fun invoke(): SyncStatus {
        val snapshot = getSnapshot()
        val state = syncState.get()
        return SyncStatus(
            hasUnsyncedChanges = !snapshot.isEmpty && state.syncedFingerprint != snapshot.fingerprint(),
            lastSyncedAt = state.lastSyncedAt,
        )
    }
}
