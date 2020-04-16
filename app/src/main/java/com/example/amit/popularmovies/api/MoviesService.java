package com.example.amit.popularmovies.api;

import com.example.amit.popularmovies.model.DiscoverMovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesService {
    @GET("/3/discover/movie")
    Call<DiscoverMovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("include_video") String includeVideos,
                                                 @Query("sort_by") String sortBy, @Query("page") String page);
}
