package com.balajiss.movie

import android.app.Application
import android.content.Context
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

        picasso = Picasso.Builder(this).build()
        Picasso.setSingletonInstance(picasso)
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