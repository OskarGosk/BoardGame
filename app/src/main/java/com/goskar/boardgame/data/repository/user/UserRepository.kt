package com.goskar.boardgame.data.repository.user

import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.rest.RequestResult

interface UserRepository {

    suspend fun logIn(user: User): RequestResult<Boolean>

    suspend fun getCurrentSession(): User?

    suspend fun logout(): RequestResult<Boolean>

    suspend fun isLoggedIn(): Boolean
}