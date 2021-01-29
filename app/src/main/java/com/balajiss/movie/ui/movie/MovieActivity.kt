package com.balajiss.movie.ui.movie

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.balajiss.movie.R
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.ui.BaseActivity
import com.balajiss.movie.ui.search.SearchFragmentDirections
import com.balajiss.movie.viewmodel.MovieViewModel
import com.balajiss.movie.viewmodel.MovieViewModelFactory

class MovieActivity : BaseActivity() {

    override fun layoutRes() = R.layout.activity_movie

    lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieViewModel = ViewModelProvider(
            this,
            MovieViewModelFactory()
        ).get(MovieViewModel::class.java)
    }

    fun openDetailFragment(item: MovieItem) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(item)
        findNavController(R.id.fragment_container).navigate(action)
    }

    override fun observe() {

    }

    override fun removeObservers() {

    }
}