package com.balajiss.movie.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balajiss.movie.network.NetworkUtil

abstract class BaseActivity : AppCompatActivity() {

    abstract fun layoutRes(): Int

    abstract fun observe()
    abstract fun removeObservers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutRes())

        observe()
    }

    override fun onDestroy() {
        super.onDestroy()

        removeObservers()
    }

    fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    fun isNetworkAvailable() = NetworkUtil(applicationContext).checkInternet()
}