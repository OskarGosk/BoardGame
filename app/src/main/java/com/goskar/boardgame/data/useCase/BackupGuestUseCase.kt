package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.guestBackup.GuestBackupRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult

/** Manual backup of a guest's local data to this device's cloud backup. */
class BackupGuestUseCase(
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val guestBackup: GuestBackupRepository,
    private val syncState: SyncStateRepository,
) {
    suspend operator fun invoke(): Boolean {
        val snapshot = getSnapshot()
        if (guestBackup.upload(snapshot) !is RequestResult.Success) return false
        syncState.markSynced(snapshot.fingerprint())
        return true
    }
}
