package com.goskar.boardgame.data.repository.syncState

data class SyncState(
    /** Fingerprint of the local data at the last successful upload or download; null if never synced. */
    val syncedFingerprint: String? = null,
    val lastSyncedAt: Long? = null,
    /** True once the guest was offered (or declined) restoring a device backup. */
    val guestRestoreResolved: Boolean = false,
)

/** Remembers what was last synced so the app can warn about unsynced local data. Cleared on sign out. */
interface SyncStateRepository {

    suspend fun get(): SyncState

    suspend fun markSynced(fingerprint: String)

    suspend fun markGuestRestoreResolved()

    suspend fun clear()
}
