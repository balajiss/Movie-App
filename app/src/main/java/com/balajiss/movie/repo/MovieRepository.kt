package com.balajiss.movie.repo

import androidx.lifecycle.MutableLiveData
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.display.MovieDisplayResponse
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.model.search.MovieSearchResponse
import com.balajiss.movie.network.MovieRetrofit
import com.balajiss.movie.network.MovieService
import com.balajiss.movie.network.NetworkResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieRepository {

    private var movieService: MovieService = MovieRetrofit.getInstance().getService()

    fun getMovieList(movieSearchRequest: MovieSearchRequest): MutableLiveData<NetworkResponse<MovieSearchResponse>> {
        val movieListLiveData = MutableLiveData<NetworkResponse<MovieSearchResponse>>()

        movieService.getMovieList(movieSearchRequest.title, movieSearchRequest.page, movieSearchRequest.type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieSearchResponse> {
                override fun onSubscribe(d: Disposable) {
                    movieListLiveData.value = NetworkResponse.Loading()
                }

                override fun onError(e: Throwable) {
                    movieListLiveData.value = NetworkResponse.Error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: MovieSearchResponse) {
                    movieListLiveData.value = NetworkResponse.Success(t)
                }
            })

        return movieListLiveData
    }

    fun getMovieDetails(movieDisplayRequest: MovieDisplayRequest): MutableLiveData<NetworkResponse<MovieDisplayResponse>> {
        val movieDetailLiveData = MutableLiveData<NetworkResponse<MovieDisplayResponse>>()

        movieService.getMovieDetail(movieDisplayRequest.imdbNo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieDisplayResponse> {
                override fun onSubscribe(d: Disposable) {
                    movieDetailLiveData.value = NetworkResponse.Loading()
                }

                override fun onError(e: Throwable) {
                    movieDetailLiveData.value = NetworkResponse.Error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: MovieDisplayResponse) {
                    movieDetailLiveData.value = NetworkResponse.Success(t)
                }
            })

        return movieDetailLiveData
    }
}