package com.balajiss.movie.ui.movie

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import com.balajiss.movie.R
import com.balajiss.movie.ui.BaseActivity
import com.balajiss.movie.ui.search.SearchFragment
import com.balajiss.movie.viewmodel.MovieViewModel
import com.balajiss.movie.viewmodel.MovieViewModelFactory

class MovieActivity : BaseActivity() {

    override fun layoutRes() = R.layout.activity_movie

    lateinit var movieViewModel: MovieViewModel
    lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieViewModel = ViewModelProvider(
            this,
            MovieViewModelFactory()
        ).get(MovieViewModel::class.java)

        searchFragment = if (savedInstanceState == null) {
            SearchFragment()
        } else {
            supportFragmentManager.getFragment(
                savedInstanceState,
                SearchFragment::class.java.name
            ) as SearchFragment
        }

        if (::searchFragment.isInitialized) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, searchFragment)
                commit()
            }
        } else {
            showToast(getString(R.string.no_view))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.movie_menu, menu)
        menu?.let {
            val searchViewItem = it.findItem(R.id.app_bar_search)
            val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView
            searchView.maxWidth = Int.MAX_VALUE
            searchView.queryHint = movieViewModel.title

            setTitleBarText()

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()

                    query?.let { queryString ->
                        if (queryString.isNotEmpty()) {
                            movieViewModel.title = queryString
                            searchFragment.clearData()
                            searchFragment.fetchData()

                            setTitleBarText()
                        }
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?) = false

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setTitleBarText() {
        title =
            movieViewModel.title.substring(0, 1).toUpperCase() + movieViewModel.title.substring(1)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        supportFragmentManager.putFragment(
            outState,
            SearchFragment::class.java.name,
            searchFragment
        )
    }

    override fun observe() {

    }

    override fun removeObservers() {

    }
}