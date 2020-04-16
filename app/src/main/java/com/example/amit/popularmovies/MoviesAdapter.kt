package com.example.amit.popularmovies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amit.popularmovies.MoviesAdapter.MoviesAdapterViewHolder
import com.example.amit.popularmovies.model.MovieDiscoverResult
import com.squareup.picasso.Picasso

class MoviesAdapter internal constructor(private val mContext: Context?, private val mClickHandler: MoviesAdapterOnClickHandler, private val mEmptyView: View, choiceMode: Int) : RecyclerView.Adapter<MoviesAdapterViewHolder>() {
    private var movieResults: List<MovieDiscoverResult>? = null
    private val mICM: ItemChoiceManager

    inner class MoviesAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val nameView: TextView
        val posterView: ImageView
        override fun onClick(v: View) {
            val adapterPosition = adapterPosition
            mClickHandler.onClick(movieResults!![adapterPosition], this)
            mICM.onClick(this)
        }

        init {
            nameView = view.findViewById(R.id.list_item_name_textview)
            posterView = view.findViewById(R.id.mPosterView)
            view.setOnClickListener(this)
        }
    }

    interface MoviesAdapterOnClickHandler {
        fun onClick(movieDiscoverResult: MovieDiscoverResult?, vh: MoviesAdapterViewHolder)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MoviesAdapterViewHolder {
        return if (viewGroup is RecyclerView) {
            var layoutId = -1
            layoutId = R.layout.list_item_movies
            val view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false)
            view.isFocusable = true
            MoviesAdapterViewHolder(view)
        } else {
            throw RuntimeException("Not bound to RecyclerView")
        }
    }

    override fun onBindViewHolder(moviesAdapterViewHolder: MoviesAdapterViewHolder, position: Int) {
        val item = movieResults!![position]
        val name = item.title
        val poster_base_url = "http://image.tmdb.org/t/p/w342"
        val posterPath = poster_base_url + item.poster_path
        moviesAdapterViewHolder.nameView.text = name
        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.ic_terrain_black_48dp)
                .error(R.drawable.ic_error_black_48dp)
                .into(moviesAdapterViewHolder.posterView)
        mICM.onBindViewHolder(moviesAdapterViewHolder, position)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mICM.onRestoreInstanceState(savedInstanceState)
    }

    fun onSaveInstanceState(outState: Bundle) {
        mICM.onSaveInstanceState(outState)
    }

    val selectedItemPosition: Int
        get() = mICM.selectedItemPosition

    override fun getItemCount(): Int {
        return if (null == movieResults) 0 else movieResults!!.size
    }

    fun setMoviesData(newCursor: List<MovieDiscoverResult>?) {
        movieResults = newCursor
        notifyDataSetChanged()
        mEmptyView.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
    }

    fun selectView(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder is MoviesAdapterViewHolder) {
            val vfh = viewHolder
            vfh.onClick(vfh.itemView)
        }
    }

    init {
        mICM = ItemChoiceManager(this)
        mICM.setChoiceMode(choiceMode)
    }
}