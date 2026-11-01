package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.db.SyncDao
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Downloads users/{uid}, merges it with the local library (see [CloudMerge]), stores the result
 * locally and uploads it only when the cloud copy differs. Returns false when any step failed;
 * the local library is then either untouched or already merged, never partially written.
 */
class SyncWithCloudUseCase(
    private val api: BoardGameFirebaseDataRepository,
    private val migrateLegacyCloudData: MigrateLegacyCloudDataUseCase,
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val syncDao: SyncDao,
    private val syncState: SyncStateRepository,
) {
    private val mutex = Mutex()
    private val attempted = AtomicBoolean(false)

    /** Syncs only if nothing has tried to sync since the app started; returns true if it synced. */
    suspend fun syncOnStartup(): Boolean = !attempted.get() && invoke()

    suspend operator fun invoke(): Boolean = mutex.withLock {
        attempted.set(true)
        try {
            sync()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.tag(TAG).e("Sync failed\n  ${e.stackTraceToString()}")
            false
        }
    }

    private suspend fun sync(): Boolean {
        if (!migrateLegacyCloudData()) return false
        val remote = (api.getUserData() as? RequestResult.Success)?.data?.toSyncData() ?: return false
        val local = SyncData(getSnapshot(), syncDao.getAllDeleted())

        val merged = CloudMerge.merge(local, remote)
        if (!merged.sameAs(local)) {
            with(merged.snapshot) { syncDao.replaceAll(games, players, history, historyExpansions, merged.deleted) }
        }
        if (!merged.sameAs(remote) && api.uploadAll(merged.toCloudUserData()) !is RequestResult.Success) return false

        syncState.markSynced(merged.snapshot.fingerprint())
        return true
    }

    private companion object {
        const val TAG = "CLOUD_SYNC"
    }
}
