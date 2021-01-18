package com.balajiss.movie.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balajiss.movie.R
import com.balajiss.movie.model.search.MovieItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MovieListAdapter(private val itemClickListener : MovieSelectListener) : RecyclerView.Adapter<MovieViewHolder>() {

    var data = ArrayList<MovieItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)

        return MovieViewHolder(view, itemClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.onBind(data[position])
    }
}

class MovieViewHolder(
    itemView: View,
    private val itemClickListener: MovieSelectListener
) : RecyclerView.ViewHolder(itemView) {

    private val movieTitle = itemView.findViewById<TextView>(R.id.title)
    private val movieImage = itemView.findViewById<ImageView>(R.id.movie_image)

    fun onBind(item: MovieItem) {
        Picasso.get().load(item.poster).error(R.drawable.broken_image).into(movieImage, object : Callback {
            override fun onSuccess() {
                itemView.visibility = View.VISIBLE
                movieTitle.text = item.title
            }

            override fun onError(e: Exception?) {
                itemView.visibility = View.VISIBLE
                movieTitle.text = item.title
            }
        })

        itemView.setOnClickListener {
            itemClickListener.onClick(item)
        }
    }
}
