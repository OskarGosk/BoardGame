package com.goskar.boardgame.data.rest

sealed class RequestResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : RequestResult<T>()
    data class Error(val throwable: Throwable) : RequestResult<Nothing>()
    data class SuccessWithData(val data: List<Any>) : RequestResult<Nothing>()

}