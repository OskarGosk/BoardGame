package com.goskar.boardgame.data.repository.guestBackup

import android.content.Context
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.models.GuestBackupDto
import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.rest.ApiGuestBackup
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import com.goskar.boardgame.utils.convertHistoryGameListToFirebase
import com.goskar.boardgame.utils.sha256Hex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * The backup is keyed by a hash of ANDROID_ID, which survives reinstalling the app (it only changes on
 * factory reset or when the app is signed with a different key). Guests have no Firebase account, so
 * every call signs in anonymously and signs out again, which keeps the rest of the app from mistaking
 * the anonymous user for a real login.
 */
class GuestBackupRepositoryImpl(
    private val context: Context,
    private val api: ApiGuestBackup,
) : GuestBackupRepository {

    private val authMutex = Mutex()

    override suspend fun getBackupInfo(): RequestResult<GuestBackupInfo> =
        request("get backup info") { hash, token ->
            val meta = api.getMeta(hash, token)
            if (meta == null) GuestBackupInfo.None else GuestBackupInfo.Available(meta)
        }

    override suspend fun upload(snapshot: LocalSnapshot): RequestResult<Boolean> =
        request("upload backup") { hash, token ->
            val backup = GuestBackupDto(
                meta = GuestBackupMeta(
                    updatedAt = System.currentTimeMillis(),
                    gameCount = snapshot.games.size,
                    playerCount = snapshot.players.size,
                    sessionCount = snapshot.history.size,
                ),
                game = snapshot.games.map { it.copy(uri = null) }.associateBy { it.id },
                player = snapshot.players.associateBy { it.id },
                historyGame = convertHistoryGameListToFirebase(snapshot.history).associateBy { it.id },
                historyGameExpansion = snapshot.historyExpansions.associateBy { it.id },
            )
            check(api.upload(hash, token, backup).isSuccessful) { "Backup upload rejected" }
            true
        }

    override suspend fun download(): RequestResult<LocalSnapshot> =
        request("download backup") { hash, token ->
            val backup = checkNotNull(api.download(hash, token)) { "No backup for this device" }
            LocalSnapshot(
                games = backup.game.orEmpty().map { (id, game) -> game.copy(id = id) },
                players = backup.player.orEmpty().map { (id, player) -> player.copy(id = id) },
                history = convertHistoryGameListToDto(
                    backup.historyGame.orEmpty().map { (id, history) -> history.copy(id = id) }
                ),
                historyExpansions = backup.historyGameExpansion.orEmpty()
                    .map { (id, expansion) -> expansion.copy(id = id) },
            )
        }

    private suspend fun <T : Any> request(
        action: String,
        block: suspend (deviceHash: String, token: String) -> T,
    ): RequestResult<T> = withContext(Dispatchers.IO) {
        runCatching { withToken { token -> block(deviceHash(), token) } }
            .onFailure { Timber.tag(TAG).e("Failed to $action\n  ${it.stackTraceToString()}") }
            .fold(onSuccess = { RequestResult.Success(it) }, onFailure = { RequestResult.Error(it) })
    }

    private suspend fun <T> withToken(block: suspend (token: String) -> T): T = authMutex.withLock {
        val auth = FirebaseAuth.getInstance()
        val signedInUser = auth.currentUser
        val user = signedInUser
            ?: checkNotNull(auth.signInAnonymously().await().user) { "Anonymous sign-in failed" }
        try {
            val token = checkNotNull(user.getIdToken(false).await().token) { "No ID token" }
            block(token)
        } finally {
            if (signedInUser == null) auth.signOut()
        }
    }

    private fun deviceHash(): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        check(!androidId.isNullOrBlank()) { "ANDROID_ID unavailable" }
        return sha256Hex("$androidId:$HASH_SALT")
    }

    private companion object {
        const val TAG = "GUEST_BACKUP"
        const val HASH_SALT = "boardgame-guest-backup"
    }
}
