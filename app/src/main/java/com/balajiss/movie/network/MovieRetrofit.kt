package com.balajiss.movie.network

import android.content.Context
import com.balajiss.movie.MovieApplication
import com.balajiss.movie.network.MovieRetrofit.Companion.INSTANCE
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MovieRetrofit private constructor() {

    private val BASE_URL = "http://www.omdbapi.com"
    private val connectionTimeOut = 10

    private val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    private fun buildRetrofit() = Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        client(createHttpClient())
        addConverterFactory(GsonConverterFactory.create())
        addCallAdapterFactory(rxAdapter)
    }.build()

    private fun createHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        return OkHttpClient().newBuilder().apply {
            addInterceptor(HeaderInterceptor(MovieApplication.getInstance().isInternetAvailable()))
            cache(Cache(MovieApplication.getInstance().cacheDir, 10 * 1024 * 1024))
            connectTimeout(connectionTimeOut.toLong(), TimeUnit.SECONDS)
            readTimeout(connectionTimeOut.toLong(), TimeUnit.SECONDS)
            writeTimeout(connectionTimeOut.toLong(), TimeUnit.SECONDS)
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }

    private fun getRetrofit(): Retrofit = MovieRetrofit().buildRetrofit()

    fun getService(): MovieService = getRetrofit().create(MovieService::class.java)

    companion object {
        @Volatile
        private var INSTANCE: MovieRetrofit? = null

        fun getInstance(): MovieRetrofit =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MovieRetrofit().also { INSTANCE = it }
            }
    }
}