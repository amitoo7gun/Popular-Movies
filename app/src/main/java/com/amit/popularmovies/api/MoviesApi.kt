package com.amit.popularmovies.api

import com.amit.popularmovies.model.DiscoverMovieResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {
    @GET("/3/discover/movie")
    fun getPopularMovies(@Query("api_key") apiKey: String?, @Query("include_video") includeVideos: String?,
                         @Query("sort_by") sortBy: String?, @Query("page") page: String?): Observable<DiscoverMovieResponse?>
}