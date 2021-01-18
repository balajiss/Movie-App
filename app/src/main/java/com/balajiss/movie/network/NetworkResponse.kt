package com.balajiss.movie.network

sealed class NetworkResponse<T>(
    val status: STATUS,
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Success<T>(data: T) : NetworkResponse<T>(STATUS.SUCCESS, data)

    class Error<T>(throwable: Throwable?, data: T? = null) :
        NetworkResponse<T>(STATUS.ERROR, data, throwable)

    class Loading<T>(data: T? = null) : NetworkResponse<T>(STATUS.LOADING, data)

    enum class STATUS { LOADING, SUCCESS, ERROR }
}