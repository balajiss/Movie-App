package com.balajiss.movie

import android.app.Application
import com.balajiss.movie.network.NetworkUtil
import com.squareup.picasso.Picasso

class MovieApplication : Application() {

    lateinit var picasso: Picasso

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
    }

    fun isInternetAvailable(): Boolean {
        return NetworkUtil(this).checkInternet()
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieApplication? = null

        fun getInstance() = INSTANCE!!
    }
}