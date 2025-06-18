package com.goskar.boardgame.data.rest

import com.goskar.boardgame.data.db.UserSessionDao
import okhttp3.Interceptor
import okhttp3.Response

class UIDInterceptor(
    private val userSession: UserSessionDao
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val userUID = getUserUID()

        val originalUrl = request.url.toString()

        val newUrl = originalUrl.replace("UID", userUID?:"")

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed((newRequest))

    }

    private fun getUserUID(): String? = userSession.getUserUID()

}
