package com.balajiss.movie.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balajiss.movie.R
import com.balajiss.movie.databinding.FragmentDetailBinding
import com.balajiss.movie.model.display.MovieDisplayRequest
import com.balajiss.movie.model.display.MovieDisplayResponse
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.network.NetworkExceptions
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.ui.BaseFragment
import com.balajiss.movie.ui.movie.MovieActivity
import com.balajiss.movie.util.MoviePicasso
import com.balajiss.movie.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.fragment_detail_content.view.*
import kotlinx.android.synthetic.main.toolbar_detail.view.*

class DetailFragment : BaseFragment() {

    override fun layoutRes() = R.layout.fragment_detail

    private lateinit var movieViewModel: MovieViewModel

    private lateinit var parent: MovieActivity

    private lateinit var dataBinding: FragmentDetailBinding

    private lateinit var selectedMovie: MovieItem

    override fun onAttach(context: Context) {
        super.onAttach(context)

        parent = context as MovieActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let { movieViewModel = ViewModelProvider(it).get(MovieViewModel::class.java) }

        if (!movieViewModel.isMovieSelected) {
            parent.showToast(getString(R.string.no_movie_selected))
        }

        arguments?.let { DetailFragmentArgs.fromBundle(it).selectedMovie }.also {
            if (it != null) {
                selectedMovie = it
            }
        }?.let { fetchDetails(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.let {
            DataBindingUtil.bind<FragmentDetailBinding>(it)?.let { bind ->
                dataBinding = bind
            }
        }

        initView()

        return dataBinding.root
    }

    private fun fetchDetails(selectedMovie: MovieItem) {
        movieViewModel.movieDetailObserver.value =
            MovieDisplayRequest(selectedMovie.imdbId)
    }

    private fun initView() {
        val toolbar: Toolbar = dataBinding.layoutToolbar.toolbar

        parent.setSupportActionBar(toolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back)
        toolbar.setNavigationOnClickListener {
            parent.onBackPressed()
        }

        parent.title = movieViewModel.selectedMovie.title

        MoviePicasso.getInstance(parent).load(selectedMovie.poster)
            .error(R.drawable.broken_image)
            .into(dataBinding.layoutToolbar.movie_image)
    }

    override fun observe() {
        movieViewModel.movieDetailObservable.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                NetworkResponse.STATUS.LOADING -> {
                    dataBinding.layoutContent.loading_details.visibility = View.VISIBLE
                    dataBinding.layoutContent.group_divider.visibility = View.GONE
                }
                NetworkResponse.STATUS.SUCCESS -> {
                    dataBinding.layoutContent.loading_details.visibility = View.GONE
                    try {
                        it.data?.let { data ->
                            if (data.Response == "True") {
                                dataBinding.layoutContent.group_divider.visibility = View.VISIBLE
                                populateView(data)
                            } else {
                                parent.showToast(data.Error)
                            }
                        }
                    } catch (e: NumberFormatException) {

                    }
                }
                NetworkResponse.STATUS.ERROR -> {
                    dataBinding.layoutContent.loading_details.visibility = View.GONE
                    dataBinding.layoutContent.group_divider.visibility = View.GONE
                    it.throwable?.let { throwable ->
                        if (throwable is NetworkExceptions.NoInternetException)
                            parent.showToast(getString(R.string.no_internet))
                    }
                }
            }
        })
    }

    private fun populateView(data: MovieDisplayResponse) {
        dataBinding.layoutContent.apply {
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
    }

    override fun removeObservers() {
        movieViewModel.movieDetailObservable.removeObservers(viewLifecycleOwner)
    }
}