package com.balajiss.movie.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balajiss.movie.R
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.display.MovieDisplayResponse
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.network.NetworkExceptions
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.ui.BaseActivity
import com.balajiss.movie.util.Constants
import com.balajiss.movie.viewmodel.DetailViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlin.text.category

class DetailActivity : BaseActivity() {

    override fun layoutRes() = R.layout.activity_detail

    lateinit var detailViewModel: DetailViewModel
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        super.onCreate(savedInstanceState)

        progressBar = findViewById(R.id.loading_details)

        intent.extras?.get(Constants.TYPE)?.let {
            detailViewModel.movieItem = it as MovieItem
        }

        val configChange = savedInstanceState?.let {
            it.getBoolean(Constants.CONFIGURATION_CHANGE)
        } ?: false

        if (!configChange) {
            fetchDetails()
        }

        initView()
    }

    private fun fetchDetails() {
        detailViewModel.movieDetailObserver.value =
            MovieDisplayRequest(detailViewModel.movieItem.imdbId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(Constants.CONFIGURATION_CHANGE, true)
    }

    private fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        title = detailViewModel.movieItem.title

        Picasso.get().load(detailViewModel.movieItem.poster).error(R.drawable.broken_image)
            .into(findViewById<ImageView>(R.id.movie_image))
    }

    override fun observe() {
        detailViewModel.movieDetailObservable.observe(this, Observer {
            when (it.status) {
                NetworkResponse.STATUS.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.data_layout).visibility = View.GONE
                }
                NetworkResponse.STATUS.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    try {
                        it.data?.let { data ->
                            if (data.Response == "True") {
                                findViewById<ConstraintLayout>(R.id.data_layout).visibility =
                                    View.VISIBLE
                                populateView(data)
                            } else {
                                showToast(data.Error)
                            }
                        }
                    } catch (e: NumberFormatException) {

                    }
                }
                NetworkResponse.STATUS.ERROR -> {
                    progressBar.visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.data_layout).visibility = View.GONE
                    it.throwable?.let { throwable ->
                        if (throwable is NetworkExceptions.NoInternetException)
                            showToast(getString(R.string.no_internet))
                    }
                }
            }
        })
    }

    private fun populateView(data: MovieDisplayResponse) {
        category.text = data.genre
        duration.text = data.runtime
        rating.text = data.imdbRating

        synopsis.text = data.plot

        score.text = data.metaScore
        language.text = data.language
        box_office.text = data.boxOffice

        director.text = data.director
        writer.text = data.writer
        actor.text = data.actors
    }

    override fun removeObservers() {
        detailViewModel.movieDetailObservable.removeObservers(this)
    }
}