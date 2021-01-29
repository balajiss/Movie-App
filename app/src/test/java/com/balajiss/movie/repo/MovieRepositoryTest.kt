package com.balajiss.movie.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.model.search.MovieSearchResponse
import com.balajiss.movie.network.MovieService
import com.balajiss.movie.util.MovieTestUtil
import com.balajiss.movie.util.RxImmediateSchedulerRule
import com.balajiss.movie.util.getOrAwaitValue
import io.reactivex.Observable
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var movieService: MovieService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        movieRepository = MovieRepository(movieService)
    }

    @Test
    fun getMovieList() {
        val mockMovieList = MovieTestUtil.mockMovieList()
        val movieSearchRequest = MovieSearchRequest()

        Mockito.`when`(
            movieService.getMovieList(
                movieSearchRequest.title,
                movieSearchRequest.page,
                movieSearchRequest.type
            )
        ).thenReturn(
            Observable.just(
                MovieSearchResponse(
                    "True",
                    "",
                    "10",
                    mockMovieList
                )
            )
        )

        val result = movieRepository.getMovieList(movieSearchRequest).getOrAwaitValue()
            .getContentIfNotHandled()

        result?.let {data->
            Assert.assertEquals(
                "True",
                data.data?.Response
            )
            Assert.assertEquals(
                "10",
                data.data?.totalResults
            )
            Assert.assertEquals(
                mockMovieList,
                data.data?.searchResult
            )
        }
    }

    @Test
    fun getMovieDetail() {
        val mockMovieDetail = MovieTestUtil.mockMovieDetail()
        val movieDisplayRequest = MovieDisplayRequest("tt4154664")

        Mockito.`when`(
            movieService.getMovieDetail(
                movieDisplayRequest.imdbNo
            )
        ).thenReturn(
            Observable.just(
                mockMovieDetail
            )
        )

        val result = movieRepository.getMovieDetails(movieDisplayRequest).getOrAwaitValue()

        Assert.assertEquals(
            "True",
            result.data?.Response
        )
        Assert.assertEquals(
            mockMovieDetail,
            result.data
        )
    }

    @After
    fun tearDown() {

    }
}