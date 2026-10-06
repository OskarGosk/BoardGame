package com.goskar.boardgame.data.rest

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.db.UserSessionDao
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

class FirebaseAuthenticator(
    private val userSessionDao: UserSessionDao
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Timber.tag("FirebaseAuthenticator").d("Authenticating 401 response")

        val user = FirebaseAuth.getInstance().currentUser ?: return null

        return try {
            // Tasks.await is blocking, which is fine here because OkHttp calls authenticator on a background thread
            val result = Tasks.await(user.getIdToken(true))
            val newToken = result.token ?: return null

            // Update local database blocking
            runBlocking {
                val currentUser = userSessionDao.current()
                if (currentUser != null) {
                    userSessionDao.insert(currentUser.copy(token = newToken))
                }
            }

            // Retry the request with the new token
            response.request.newBuilder()
                .url(response.request.url.newBuilder()
                    .setQueryParameter("auth", newToken)
                    .build())
                .build()
        } catch (e: Exception) {
            Timber.tag("FirebaseAuthenticator").e(e, "Failed to refresh token")
            null
        }
    }
}