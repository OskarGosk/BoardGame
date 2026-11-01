package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult

/**
 * Accounts synced before 2.0.3 keep their data under game/{uid}, player/{uid}… This copies it once
 * to users/{uid}; the old nodes stay untouched so 2.0.2 keeps working until everyone updates.
 * Returns false only when the cloud could not be checked or written.
 */
class MigrateLegacyCloudDataUseCase(
    private val api: BoardGameFirebaseDataRepository,
) {
    suspend operator fun invoke(): Boolean {
        val hasUserData = api.hasUserData() as? RequestResult.Success ?: return false
        if (hasUserData.data) return true

        val legacy = api.getLegacyData() as? RequestResult.Success ?: return false
        if (legacy.data.isEmpty) return true

        return api.uploadAll(legacy.data) is RequestResult.Success
    }
}
