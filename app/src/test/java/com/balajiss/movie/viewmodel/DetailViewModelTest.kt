package com.balajiss.movie.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.balajiss.movie.util.MovieTestUtil.mockMovieDetail
import com.balajiss.movie.util.MovieTestUtil.mockMovieList
import com.balajiss.movie.util.RxImmediateSchedulerRule
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.repo.MovieRepository
import com.balajiss.movie.util.getOrAwaitValue
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var detailViewModel: DetailViewModel

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        detailViewModel = DetailViewModel(movieRepository)
    }

    @Test
    fun getMovieList() {
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

        detailViewModel.movieDetailObserver.value = movieDisplayRequest

        val result = detailViewModel.movieDetailObservable.getOrAwaitValue()

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