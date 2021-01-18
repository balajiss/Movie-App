package com.balajiss.movie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment(): Fragment() {

    abstract fun layoutRes(): Int

    abstract fun observe()
    abstract fun removeObserve()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        removeObserve()
    }
}