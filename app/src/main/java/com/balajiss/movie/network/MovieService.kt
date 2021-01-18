package com.balajiss.movie.network

import com.balajiss.movie.model.display.MovieDisplayResponse
import com.balajiss.movie.model.search.MovieSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("/")
    fun getMovieList(
        @Query("s") title: String,
        @Query("page") page: Int,
        @Query("type") type: String
    ): Observable<MovieSearchResponse>

    @GET("/")
    fun getMovieDetail(
        @Query("i") imdbNo: String
    ): Observable<MovieDisplayResponse>
}