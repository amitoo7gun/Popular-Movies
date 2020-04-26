package com.amit.popularmovies.ui.utils

import com.amit.popularmovies.model.MovieDiscoverResult

interface MoviesNavigator {

    fun onMovieItemClick(movieDiscoverResult : MovieDiscoverResult)
}