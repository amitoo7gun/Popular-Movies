package com.amit.popularmovies.ui.main

import androidx.lifecycle.MutableLiveData
import com.amit.popularmovies.api.POSTER_BASE_URL
import com.amit.popularmovies.base.BaseViewModel
import com.amit.popularmovies.model.MovieDiscoverResult

class MovieViewModel : BaseViewModel() {
    private val moviePosterPath = MutableLiveData<String>()

    fun bind(movie: MovieDiscoverResult){
        moviePosterPath.value = POSTER_BASE_URL + movie.poster_path
    }

    fun getPosterPath():MutableLiveData<String>{
        return moviePosterPath
    }
}