package com.balajiss.movie.network

import com.balajiss.movie.model.search.MovieSearchRequest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MovieServiceTest : RetrofitAbstract<MovieService>() {

    private lateinit var movieService: MovieService

    @Before
    fun initService() {
        movieService = createService(MovieService::class.java)
    }

    @Test
    fun getMovieList() {
        val movieSearchRequest = MovieSearchRequest()

        enqueueResponse("movieList.json")
        val response = movieService.getMovieList(
            movieSearchRequest.title,
            movieSearchRequest.page,
            movieSearchRequest.type
        )
        val responseObj = response.blockingFirst()
        val responseBody = responseObj.searchResult
        mockWebServer.takeRequest()

        Assert.assertThat(responseObj.Response, `is`("True"))
        Assert.assertThat(responseBody[0].title, `is`("Captain Marvel"))
        Assert.assertThat(responseBody[0].imdbId, `is`("tt4154664"))
        Assert.assertThat(responseBody[0].year, `is`("2019"))
    }

    @Test
    fun getMovieDetail() {
        enqueueResponse("movieDetail.json")
        val response = movieService.getMovieDetail("tt4154664")
        val responseBody = response.blockingFirst()
        mockWebServer.takeRequest()

        Assert.assertThat(responseBody.Response, `is`("True"))
        Assert.assertThat(responseBody.title, `is`("Captain Marvel"))
        Assert.assertThat(responseBody.imdbId, `is`("tt4154664"))
        Assert.assertThat(responseBody.released, `is`("08 Mar 2019"))
        Assert.assertThat(responseBody.rated, `is`("PG-13"))
        Assert.assertThat(responseBody.genre, `is`("Action, Adventure, Sci-Fi"))
    }
}