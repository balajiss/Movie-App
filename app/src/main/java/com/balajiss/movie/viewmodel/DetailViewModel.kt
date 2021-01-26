package com.balajiss.movie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.repo.MovieRepository

class DetailViewModel(private val movieRepository: MovieRepository = MovieRepository()) :
    ViewModel() {
    lateinit var movieItem: MovieItem

    val movieDetailObserver = MutableLiveData<MovieDisplayRequest>()

    val movieDetailObservable = Transformations.switchMap(movieDetailObserver) {
        movieDetailObserver.value?.let { movieDisplayRequest ->
            movieRepository.getMovieDetails(movieDisplayRequest)
        }
    }
}