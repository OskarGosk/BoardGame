package com.goskar.boardgame.data.rest

import timber.log.Timber

suspend fun <T : Any> safeCall(
    tag: String,
    errorMsg: String,
    call: suspend () -> T,
): RequestResult<T> = runCatching { call() }
    .onFailure { Timber.tag(tag).e("$errorMsg\n  ${it.stackTraceToString()}") }
    .fold(
        onSuccess = { RequestResult.Success(it) },
        onFailure = { RequestResult.Error(it) },
    )
