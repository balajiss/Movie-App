package com.balajiss.movie.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.util.MovieTestUtil.mockMovieList
import com.balajiss.movie.util.RxImmediateSchedulerRule
import com.balajiss.movie.util.getOrAwaitValue
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.model.search.MovieSearchResponse
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.repo.MovieRepository
import com.balajiss.movie.util.Event
import com.balajiss.movie.util.MovieTestUtil.mockMovieDetail
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieViewModelTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var movieViewModel: MovieViewModel

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        movieViewModel = MovieViewModel(movieRepository)
    }

    @Test
    fun getMovieList() {
        val mockMovieList = mockMovieList()

        val movieSearchRequest = MovieSearchRequest()

        Mockito.`when`(
            movieRepository.getMovieList(movieSearchRequest)
        ).thenReturn(
            MutableLiveData(
                Event<NetworkResponse<MovieSearchResponse>>(
                    NetworkResponse.Success(
                        MovieSearchResponse(
                            "True",
                            "",
                            "10",
                            mockMovieList
                        )
                    )
                )
            )
        )

        movieViewModel.movieSearchRequestObservable.value = movieSearchRequest

        val result = movieViewModel.movieListObservable.getOrAwaitValue().getContentIfNotHandled()

        result?.let { data ->
            Assert.assertEquals(
                NetworkResponse.STATUS.SUCCESS,
                data.status
            )
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
        val mockDisplayData = mockMovieDetail()

        val movieDisplayRequest = MovieDisplayRequest("tt4154664")

        Mockito.`when`(
            movieRepository.getMovieDetails(movieDisplayRequest)
        ).thenReturn(
            MutableLiveData(
                NetworkResponse.Success(
                    mockDisplayData
                )
            )
        )

        movieViewModel.movieDetailObserver.value = movieDisplayRequest

        val result = movieViewModel.movieDetailObservable.getOrAwaitValue()

        Assert.assertEquals(
            NetworkResponse.STATUS.SUCCESS,
            result.status
        )
        Assert.assertEquals(
            "True",
            result.data?.Response
        )
        Assert.assertEquals(
            mockDisplayData,
            result.data
        )
    }

    @After
    fun tearDown() {

    }
}