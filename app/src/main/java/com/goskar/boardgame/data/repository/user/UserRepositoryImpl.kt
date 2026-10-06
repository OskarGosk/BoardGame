package com.goskar.boardgame.data.repository.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.data.db.UserSessionDao
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepositoryImpl
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserRepositoryImpl(
    private val userSessionDao: UserSessionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    companion object {
        const val TAG = "USER_REPOSITORY"
    }

    override suspend fun logIn(user: User): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                userSessionDao.insert(user)
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't logIn\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun getCurrentSession(): User? {
        return runCatching {
            val response = userSessionDao.current()
            if (response?.token.isNullOrBlank()) throw IllegalStateException("Token is null")
            response
        }.onFailure {
            Timber.tag(TAG).e("An error has occured while providing User from Database.")
        }.fold(onSuccess = { it }, onFailure = { null })
    }

    override suspend fun logout(): RequestResult<Boolean> {
        return withContext(defaultDispatcher) {
            runCatching {
                userSessionDao.logout(User(
                    id = 0,
                    email = null,
                    token = null,
                    userUID = null
                ))
            }.onFailure {
                Timber.tag(GameDbRepositoryImpl.TAG).e("Can't logOut user\n ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = { RequestResult.Success(true) }, onFailure = { RequestResult.Error(it) })
    }

    override suspend fun isLoggedIn(): Boolean {
        return getCurrentSession() != null
    }

    override fun startTokenMonitoring() {
        FirebaseAuth.getInstance().addIdTokenListener(FirebaseAuth.IdTokenListener { auth ->
            val user: FirebaseUser? = auth.currentUser
            user?.getIdToken(false)?.addOnSuccessListener { result ->
                val newToken = result.token
                if (newToken != null) {
                    CoroutineScope(defaultDispatcher).launch {
                        val currentUser = userSessionDao.current()
                        if (currentUser != null) {
                            userSessionDao.insert(currentUser.copy(token = newToken))
                            Timber.tag(TAG).d("Token updated via IdTokenListener")
                        }
                    }
                }
            }
        })
    }
}