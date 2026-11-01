package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.rest.RequestResult

interface BoardGameFirebaseDataRepository {

    suspend fun getUserData(): RequestResult<CloudUserData>

    suspend fun uploadAll(data: CloudUserData): RequestResult<Boolean>

    suspend fun hasUserData(): RequestResult<Boolean>
    suspend fun getLegacyData(): RequestResult<CloudUserData>
}
