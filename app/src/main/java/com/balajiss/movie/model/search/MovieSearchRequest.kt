package com.balajiss.movie.model.search

import com.balajiss.movie.util.Constants
import retrofit2.http.Query

data class MovieSearchRequest(
    @Query("s") val title: String = Constants.DEFAULT_SEARCH,
    @Query("page") val page: Int = Constants.PAGE,
    @Query("type") val type: String = Constants.TYPE
)