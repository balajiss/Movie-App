package com.balajiss.movie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.model.search.MovieSearchResponse
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.repo.MovieRepository
import com.balajiss.movie.util.Constants
import com.balajiss.movie.util.Event

class MovieViewModel(private val movieRepository: MovieRepository = MovieRepository()) : ViewModel() {

    lateinit var selectedMovie: MovieItem
    val isMovieSelected get() = this::selectedMovie.isInitialized
    val searchData = ArrayList<MovieItem>()

    var title: String = Constants.DEFAULT_SEARCH
    var totalItems: Int = 0

    val movieSearchRequestObservable = MutableLiveData<MovieSearchRequest>()

    val movieListObservable: LiveData<Event<NetworkResponse<MovieSearchResponse>>> = Transformations.switchMap(movieSearchRequestObservable) {
        movieSearchRequestObservable.value?.let { movieSearchRequest ->
            movieRepository.getMovieList(
                movieSearchRequest
            )
        }
    }

    val movieDetailObserver = MutableLiveData<MovieDisplayRequest>()

    val movieDetailObservable = Transformations.switchMap(movieDetailObserver) {
        movieDetailObserver.value?.let { movieDisplayRequest ->
            movieRepository.getMovieDetails(movieDisplayRequest)
        }
    }
}