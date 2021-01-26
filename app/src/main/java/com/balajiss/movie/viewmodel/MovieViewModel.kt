package com.balajiss.movie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.repo.MovieRepository
import com.balajiss.movie.util.Constants

class MovieViewModel(private val movieRepository: MovieRepository = MovieRepository()) : ViewModel() {

    lateinit var selectedMovie: MovieItem
    val searchData = ArrayList<MovieItem>()

    var title: String = Constants.DEFAULT_SEARCH
    var totalItems: Int = 0

    val movieSearchRequestObservable = MutableLiveData<MovieSearchRequest>()

    val movieListObservable = Transformations.switchMap(movieSearchRequestObservable) {
        movieSearchRequestObservable.value?.let { movieSearchRequest ->
            movieRepository.getMovieList(
                movieSearchRequest
            )
        }
    }
}