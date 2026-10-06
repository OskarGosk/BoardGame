package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.repository.guestBackup.GuestBackupInfo
import com.goskar.boardgame.data.repository.guestBackup.GuestBackupRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult

/**
 * Offers a guest the backup made on this device, e.g. after reinstalling the app. The offer is made
 * only once and only while the local database is empty, so existing data is never overwritten.
 */
class GuestRestoreUseCase(
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val guestBackup: GuestBackupRepository,
    private val syncState: SyncStateRepository,
    private val upsertGames: UpsertAllGameUseCase,
    private val upsertPlayers: UpsertAllPlayerUseCase,
    private val upsertHistory: UpsertAllHistoryGameUseCase,
    private val upsertExpansions: UpsertAllHistoryGameExpansionUseCase,
) {

    /** The backup to offer, or null when there is nothing to offer (or it could not be checked yet). */
    suspend fun findOffer(): GuestBackupMeta? {
        if (syncState.get().guestRestoreResolved) return null
        if (!getSnapshot().isEmpty) {
            syncState.markGuestRestoreResolved()
            return null
        }
        return when (val info = guestBackup.getBackupInfo()) {
            is RequestResult.Success -> when (val backup = info.data) {
                is GuestBackupInfo.Available -> backup.meta
                GuestBackupInfo.None -> {
                    syncState.markGuestRestoreResolved()
                    null
                }
            }
            // Offline or rejected: stay unresolved and ask again next time.
            is RequestResult.Error -> null
        }
    }

    suspend fun restore(): Boolean {
        val backup = (guestBackup.download() as? RequestResult.Success)?.data ?: return false
        val saved = upsertGames(backup.games) &&
            upsertPlayers(backup.players) &&
            upsertHistory(backup.history) &&
            upsertExpansions(backup.historyExpansions)
        if (!saved) return false
        syncState.markSynced(getSnapshot().fingerprint())
        syncState.markGuestRestoreResolved()
        return true
    }

    suspend fun decline() = syncState.markGuestRestoreResolved()
}
