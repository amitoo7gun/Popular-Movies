package com.example.amit.popularmovies.api

import com.example.amit.popularmovies.model.DiscoverMovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("/3/discover/movie")
    fun getPopularMovies(@Query("api_key") apiKey: String?, @Query("include_video") includeVideos: String?,
                         @Query("sort_by") sortBy: String?, @Query("page") page: String?): Call<DiscoverMovieResponse?>?
}