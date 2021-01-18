package com.balajiss.movie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MovieViewModel::class.java) {
            MovieViewModel() as T
        } else {
            super.create(modelClass)
        }
    }
}