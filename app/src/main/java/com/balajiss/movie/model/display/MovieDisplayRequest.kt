package com.balajiss.movie.model.display

import retrofit2.http.Query

data class MovieDisplayRequest(
    @Query("i") val imdbNo: String
)