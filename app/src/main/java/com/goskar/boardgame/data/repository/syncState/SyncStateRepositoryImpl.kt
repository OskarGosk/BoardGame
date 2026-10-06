package com.goskar.boardgame.data.repository.syncState

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.syncStateDataStore by preferencesDataStore(name = "sync_state")

class SyncStateRepositoryImpl(
    private val context: Context,
) : SyncStateRepository {

    private val fingerprintKey = stringPreferencesKey("synced_fingerprint")
    private val lastSyncedAtKey = longPreferencesKey("last_synced_at")
    private val guestRestoreResolvedKey = booleanPreferencesKey("guest_restore_resolved")

    override suspend fun get(): SyncState {
        val prefs = context.syncStateDataStore.data.first()
        return SyncState(
            syncedFingerprint = prefs[fingerprintKey],
            lastSyncedAt = prefs[lastSyncedAtKey],
            guestRestoreResolved = prefs[guestRestoreResolvedKey] ?: false,
        )
    }

    override suspend fun markSynced(fingerprint: String) {
        context.syncStateDataStore.edit {
            it[fingerprintKey] = fingerprint
            it[lastSyncedAtKey] = System.currentTimeMillis()
        }
    }

    override suspend fun markGuestRestoreResolved() {
        context.syncStateDataStore.edit { it[guestRestoreResolvedKey] = true }
    }

    override suspend fun clear() {
        context.syncStateDataStore.edit { it.clear() }
    }
}
