package com.balajiss.movie.ui.search

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balajiss.movie.R
import com.balajiss.movie.databinding.FragmentSearchBinding
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.model.search.MovieSearchRequest
import com.balajiss.movie.model.search.MovieSearchResponse
import com.balajiss.movie.network.NetworkExceptions
import com.balajiss.movie.network.NetworkResponse
import com.balajiss.movie.ui.BaseFragment
import com.balajiss.movie.ui.movie.MovieActivity
import com.balajiss.movie.util.Constants
import com.balajiss.movie.viewmodel.MovieViewModel
import com.balajiss.movie.viewmodel.MovieViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : BaseFragment(), MovieSelectListener {

    override fun layoutRes() = R.layout.fragment_search

    lateinit var movieViewModel: MovieViewModel

    lateinit var parent: MovieActivity

    private lateinit var dataBinding: FragmentSearchBinding

    private lateinit var adapter: MovieListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        parent = context as MovieActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        view?.let {
            DataBindingUtil.bind<FragmentSearchBinding>(it)?.let { bind ->
                dataBinding = bind
            }
        }

        view?.let { initView() }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        activity?.let {
            movieViewModel = ViewModelProvider(
                it,
                MovieViewModelFactory()
            ).get(
                MovieViewModel::class.java
            )
        }

        fetchData()
    }

    override fun observe() {
        movieViewModel.movieListObservable.observe(viewLifecycleOwner,
            Observer { event ->
                event.getContentIfNotHandled().let { data ->
                    data?.let { notHandledData ->
                        when (notHandledData.status) {
                            NetworkResponse.STATUS.LOADING -> {
                                dataBinding.layoutContent.loading_movies.visibility = View.VISIBLE
                            }
                            NetworkResponse.STATUS.SUCCESS -> {
                                dataBinding.layoutContent.loading_movies.visibility = View.GONE
                                notHandledData.data?.let { handleData(it) }
                            }
                            NetworkResponse.STATUS.ERROR -> {
                                dataBinding.layoutContent.loading_movies.visibility = View.GONE
                                notHandledData.throwable?.let { throwable ->
                                    if (throwable is NetworkExceptions.NoInternetException)
                                        parent.showToast(getString(R.string.no_internet))
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun handleData(data: MovieSearchResponse) {
        try {
            if (data.Response == "True") {
                movieViewModel.totalItems =
                    Integer.parseInt(data.totalResults)
                populateList(data.searchResult)
            } else {
                parent.showToast(data.Error)
            }
        } catch (e: NumberFormatException) {

        }
    }

    private fun populateList(searchResult: List<MovieItem>) {
        movieViewModel.searchData.addAll(searchResult)

        adapter.data.addAll(searchResult)
        adapter.notifyDataSetChanged()
    }

    private fun populateExistingData(searchResult: List<MovieItem>) {
        adapter.data.addAll(searchResult)
        adapter.notifyDataSetChanged()
    }

    override fun removeObservers() {
        movieViewModel.movieListObservable.removeObservers(viewLifecycleOwner)
    }

    private fun initView() {
        parent.setSupportActionBar(dataBinding.layoutToolbar.toolbar)

        parent.title = getString(R.string.app_name)

        initMovieListView()
    }

    private fun spanCount() = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> Constants.PORTRAIT_ROW_ITEM_NOS
        Configuration.ORIENTATION_LANDSCAPE -> Constants.LANDSCAPE_ROW_ITEM_NOS
        else -> Constants.PORTRAIT_ROW_ITEM_NOS
    }

    private fun bufferCount() = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> Constants.PORTRAIT_ROW_BUFFER_ITEM_NOS
        Configuration.ORIENTATION_LANDSCAPE -> Constants.LANDSCAPE_ROW_BUFFER_ITEM_NOS
        else -> Constants.PORTRAIT_ROW_BUFFER_ITEM_NOS
    }

    private fun initMovieListView() {
        adapter = MovieListAdapter(this)

        val layoutManager = GridLayoutManager(context, spanCount())

        dataBinding.layoutContent.movie_list.layoutManager = layoutManager
        dataBinding.layoutContent.movie_list.adapter = adapter

        populateExistingData(movieViewModel.searchData)

        dataBinding.layoutContent.movie_list.itemAnimator = DefaultItemAnimator()

        var scrolling = false

        var scrolledItems = 0
        var visibleItemCount = 0
        var totalItemCount = 0
        var previousItemCount = 0

        dataBinding.layoutContent.movie_list.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

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

                if (scrolling && (visibleItemCount + scrolledItems) >= (totalItemCount - bufferCount()) && (visibleItemCount + scrolledItems) != (previousItemCount - bufferCount())) {
                    previousItemCount = totalItemCount
                    scrolling = false

                    if (totalItemCount < movieViewModel.totalItems)
                        fetchData((totalItemCount / Constants.PAGE_ITEMS_COUNT) + 1)
                }
            }
        })
    }

    fun fetchData(page: Int = 1) {
        if (page == 1) {
            movieViewModel.searchData.clear()
            if (::adapter.isInitialized)
                adapter.lastPosition = 0
        }
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
        movieViewModel.selectedMovie = item
        parent.openDetailFragment(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_menu, menu)
        menu?.let { it ->
            val searchViewItem = it.findItem(R.id.app_bar_search)
            val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView
            searchView.maxWidth = Int.MAX_VALUE
            searchView.queryHint = movieViewModel.title

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()

                    query?.let { queryString ->
                        if (queryString.isNotEmpty()) {
                            movieViewModel.title = queryString
                            clearData()
                            fetchData()
                        }
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?) = false

            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}