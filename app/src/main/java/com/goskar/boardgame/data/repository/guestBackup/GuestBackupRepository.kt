package com.goskar.boardgame.data.repository.guestBackup

import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.rest.RequestResult

sealed interface GuestBackupInfo {
    data object None : GuestBackupInfo
    data class Available(val meta: GuestBackupMeta) : GuestBackupInfo
}

/**
 * One backup per device, so a guest can get their data back after reinstalling the app.
 * Local file covers (Game.uri) are not part of the backup.
 */
interface GuestBackupRepository {

    suspend fun getBackupInfo(): RequestResult<GuestBackupInfo>

    /** Replaces the existing backup of this device. */
    suspend fun upload(snapshot: LocalSnapshot): RequestResult<Boolean>

    suspend fun download(): RequestResult<LocalSnapshot>
}
