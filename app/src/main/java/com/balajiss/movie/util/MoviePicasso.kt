package com.balajiss.movie.util

import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient


class MoviePicasso private constructor() {

    companion object {

        @Volatile
        private var INSTANCE: Picasso? = null

        fun getInstance(context: Context): Picasso = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MoviePicasso().build(context).also { INSTANCE = it }
        }
    }

    private fun build(context: Context): Picasso {
        val picasso = Picasso.Builder(context).apply {
            downloader(OkHttp3Downloader(createHttpClient()))
        }.build()
        Picasso.setSingletonInstance(picasso)

        return picasso
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().apply {
            retryOnConnectionFailure(false)
        }.build()
    }
}