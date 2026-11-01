package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.db.UserSessionDao
import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.rest.ApiFirebaseData
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class BoardGameFirebaseDataRepositoryImpl(
    private val apiFirebaseData: ApiFirebaseData,
    private val userSession: UserSessionDao,
) : BoardGameFirebaseDataRepository {

    override suspend fun getUserData(): RequestResult<CloudUserData> =
        request("get user data") { uid -> apiFirebaseData.getUserData(uid).bodyOrNull() ?: CloudUserData() }

    override suspend fun uploadAll(data: CloudUserData): RequestResult<Boolean> =
        request("upload user data") { uid ->
            check(apiFirebaseData.updateUserData(uid, data).isSuccessful) { "Upload rejected" }
            true
        }

    override suspend fun hasUserData(): RequestResult<Boolean> =
        request("check user data") { uid -> !apiFirebaseData.getUserDataKeys(uid).bodyOrNull().isNullOrEmpty() }

    override suspend fun getLegacyData(): RequestResult<CloudUserData> =
        request("get legacy data") { uid ->
            CloudUserData(
                game = apiFirebaseData.getLegacyGames(uid).bodyOrNull(),
                player = apiFirebaseData.getLegacyPlayers(uid).bodyOrNull(),
                historyGame = apiFirebaseData.getLegacyHistoryGames(uid).bodyOrNull(),
                historyGameExpansion = apiFirebaseData.getLegacyHistoryGameExpansions(uid).bodyOrNull(),
            )
        }

    private fun <T> Response<T>.bodyOrNull(): T? {
        check(isSuccessful) { "HTTP ${code()}" }
        return body()
    }

    private suspend fun <T : Any> request(
        action: String,
        block: suspend (uid: String) -> T,
    ): RequestResult<T> = withContext(Dispatchers.IO) {
        runCatching { block(currentUid()) }
            .onFailure { Timber.tag(TAG).e("Can't $action\n  ${it.stackTraceToString()}") }
            .fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    // Without a real UID the path would collapse to users/.json and touch every account's data.
    private fun currentUid(): String =
        checkNotNull(userSession.getUserUID()?.takeIf { it.isNotBlank() && it != GUEST_UID }) {
            "No signed-in account"
        }

    private companion object {
        const val TAG = "Firebase DB"
        const val GUEST_UID = "guest"
    }
}
