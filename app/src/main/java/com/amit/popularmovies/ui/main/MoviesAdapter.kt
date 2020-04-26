package com.amit.popularmovies.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amit.popularmovies.R
import com.amit.popularmovies.databinding.ListItemMoviesBinding
import com.amit.popularmovies.model.MovieDiscoverResult
import com.amit.popularmovies.ui.utils.MoviesNavigator

class MoviesAdapter(private var moviesNavigator: MoviesNavigator) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {
    private lateinit var moviesList: List<MovieDiscoverResult>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemMoviesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_movies, parent, false)
        return ViewHolder(binding, moviesNavigator)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int {
        return if (::moviesList.isInitialized) moviesList.size else 0
    }

    fun updatePostList(moviesList: List<MovieDiscoverResult>) {
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemMoviesBinding, private val moviesNavigator: MoviesNavigator) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = MovieViewModel()
        fun bind(movieDiscoverResult: MovieDiscoverResult) {
            viewModel.bind(movieDiscoverResult)
            binding.viewModel = viewModel
            binding.callBack = moviesNavigator
        }
    }
}