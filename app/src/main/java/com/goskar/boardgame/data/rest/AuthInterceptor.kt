package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.db.UserSessionDao
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userSession: UserSessionDao
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val accessToken = getAccessToken()

        val newUrl = request.url.newBuilder()
            .apply { if (accessToken!= null) addQueryParameter("auth", accessToken) }
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed((newRequest))

    }

    private fun getAccessToken(): String? = userSession.getToken()
}