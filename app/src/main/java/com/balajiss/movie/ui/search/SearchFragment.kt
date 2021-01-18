package com.balajiss.movie.ui.search

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balajiss.movie.R
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.network.NetworkExceptions
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.ui.BaseActivity
import com.balajiss.movie.ui.BaseFragment
import com.balajiss.movie.ui.detail.DetailActivity
import com.balajiss.movie.util.Constants
import com.balajiss.movie.viewmodel.MovieViewModel
import com.balajiss.movie.viewmodel.MovieViewModelFactory


class SearchFragment : BaseFragment(), MovieSelectListener {

    override fun layoutRes() = R.layout.fragment_search

    lateinit var movieViewModel: MovieViewModel

    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView

    var configChange = false

    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        retainInstance = true

        setHasOptionsMenu(true)

        view?.let { view ->
            progressBar = view.findViewById(R.id.loading_movies)
            recyclerView = view.findViewById(R.id.movie_list)
        }

        activity?.let {
            movieViewModel = ViewModelProvider(
                it,
                MovieViewModelFactory()
            ).get(
                MovieViewModel::class.java
            )
        }

        view?.let { initView() }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(Constants.CONFIGURATION_CHANGE, true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.let {
            configChange = it.getBoolean(Constants.CONFIGURATION_CHANGE)
        }

        if (!configChange)
            fetchData()
    }

    override fun observe() {
        movieViewModel.movieListObservable.observe(this,
            Observer {
                when (it.status) {
                    NetworkResponse.STATUS.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    NetworkResponse.STATUS.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        try {
                            it.data?.let { data ->
                                if (data.Response == "True") {
                                    movieViewModel.totalItems = Integer.parseInt(data.totalResults)
                                    populateList(data.searchResult)
                                } else {
                                    (activity as BaseActivity).showToast(data.Error)
                                }
                            }
                        } catch (e: NumberFormatException) {

                        }
                    }
                    NetworkResponse.STATUS.ERROR -> {
                        progressBar.visibility = View.GONE
                        it.throwable?.let { throwable ->
                            if (throwable is NetworkExceptions.NoInternetException)
                                (activity as BaseActivity).showToast(getString(R.string.no_internet))
                        }
                    }
                }
            })
    }

    private fun populateList(searchResult: List<MovieItem>) {
        if (configChange) {
            (searchResult as ArrayList).clear()
            searchResult.addAll(movieViewModel.searchData)
            configChange = false
        } else {
            movieViewModel.searchData.addAll(searchResult)
        }
        adapter.data.addAll(searchResult)
        adapter.notifyDataSetChanged()
    }

    override fun removeObserve() {
        movieViewModel.movieListObservable.removeObservers(this)
    }

    private fun initView() {
        initMovieListView()
    }

    private fun spanCount() = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> 2
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }

    private fun initMovieListView() {
        adapter = MovieListAdapter(this)

        val layoutManager = GridLayoutManager(context, spanCount())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        recyclerView.itemAnimator = DefaultItemAnimator()

        var scrolling = false

        var scrolledItems = 0
        var visibleItemCount = 0
        var totalItemCount = 0
        var previousItemCount = 0
        var buffer = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    scrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                recyclerView.layoutManager?.let {
                    visibleItemCount = it.childCount
                    totalItemCount = it.itemCount
                    scrolledItems =
                        (it as GridLayoutManager).findFirstVisibleItemPosition()
                }

                if (scrolling && (visibleItemCount + scrolledItems) == (totalItemCount - buffer) && (visibleItemCount + scrolledItems) != (previousItemCount - buffer)) {
                    previousItemCount = totalItemCount
                    scrolling = false

                    if (totalItemCount < movieViewModel.totalItems)
                        fetchData((totalItemCount / Constants.PAGE_ITEMS_COUNT) + 1)
                }
            }
        })
    }

    fun fetchData(page: Int = 1) {
        movieViewModel.movieSearchRequestObservable.value =
            MovieSearchRequest(title = movieViewModel.title, page = page)
    }

    fun clearData() {
        adapter.let {
            it.data.clear()
            it.notifyDataSetChanged()
        }
    }

    override fun onClick(item: MovieItem) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(Constants.TYPE, item)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchViewItem = menu.findItem(R.id.app_bar_search)
        searchViewItem.isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }
}