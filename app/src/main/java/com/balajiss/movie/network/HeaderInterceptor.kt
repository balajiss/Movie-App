package com.balajiss.movie.network

import android.content.Context
import com.balajiss.movie.R
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import java.util.concurrent.TimeoutException

class HeaderInterceptor(private val isInternetAvailable: Boolean) : Interceptor {

    private val API_KEY_LABEL = "apiKey"
    private val API_KEY = "b9bd48a6"

    private val onlineCacheAge = 60
    private val offlineCacheAge = 60 * 60 * 24 * 30

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val url = request.url.newBuilder().addQueryParameter(API_KEY_LABEL, API_KEY).build()

        val requestBuilder = request.newBuilder()

        request = if (isInternetAvailable) {
            requestBuilder.header(
                "Cache-Control",
                "public, max-age=$onlineCacheAge"
            ).build()
        } else {
            requestBuilder.header(
                "Cache-Control",
                "public, max-age=$offlineCacheAge"
            ).build()
            throw NetworkExceptions.NoInternetException()
        }

        requestBuilder.url(url)

        val response = chain.proceed(requestBuilder.build())

        when (response.code) {
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> throw TimeoutException()
        }

        return response
    }
}