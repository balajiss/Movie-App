package com.balajiss.movie.ui.search

import com.balajiss.movie.model.search.MovieItem

interface MovieSelectListener {

    fun onClick(item: MovieItem)
}