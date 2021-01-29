package com.balajiss.movie.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.balajiss.movie.R
import com.balajiss.movie.databinding.ItemMovieBinding
import com.balajiss.movie.model.search.MovieItem
import com.balajiss.movie.util.MoviePicasso
import com.squareup.picasso.Callback

class MovieListAdapter(private val itemClickListener: MovieSelectListener) :
    RecyclerView.Adapter<MovieViewHolder>() {

    var data = ArrayList<MovieItem>()

    var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)

        val itemBinding = DataBindingUtil.bind<ItemMovieBinding>(view)

        return MovieViewHolder(itemBinding, itemClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        setAnimation(holder.itemView, position)

        holder.onBind(data[position])
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val pushUpAnimation =
                AnimationUtils.loadAnimation(view.context, R.anim.item_animation_push_up)
            view.startAnimation(pushUpAnimation)

            lastPosition = position
        }
    }
}

class MovieViewHolder(
    private val itemBinding: ItemMovieBinding?,
    private val itemClickListener: MovieSelectListener
) : RecyclerView.ViewHolder(itemBinding?.root!!) {

    fun onBind(item: MovieItem) {
        itemBinding?.let {
            MoviePicasso.getInstance(itemView.context).load(item.poster)
                .error(R.drawable.broken_image)
                .into(it.movieImage, object : Callback {
                    override fun onSuccess() {
                        itemView.visibility = View.VISIBLE
                        it.title.text = item.title
                    }

                    override fun onError(e: Exception?) {
                        itemView.visibility = View.VISIBLE
                        it.title.text = item.title
                    }
                })

            itemView.setOnClickListener {
                itemClickListener.onClick(item)
            }
        }
    }
}
