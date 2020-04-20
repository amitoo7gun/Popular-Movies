package com.amit.popularmovies.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiscoverMovieResponse(
        val page: Int,
        val results: List<MovieDiscoverResult>,
        val total_pages: Int,
        val total_results: Int
) : Parcelable