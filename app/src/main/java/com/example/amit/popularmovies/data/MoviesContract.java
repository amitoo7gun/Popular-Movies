package com.example.amit.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.amit.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "popularmovies";
        public static final String COLUMN_MOVIE_POSTERPATH = "posterpath";
        public static final String COLUMN_MOVIE_MOVID = "movieid";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_RELEASEDATE = "releasedate";
        public static final String COLUMN_MOVIE_USERRATING = "rating";
        public static final String COLUMN_MOVIE_PLOT = "plot";
        public static final String COLUMN_MOVIE_FAVOURITE = "isFavourite";


        public static Uri buildMoviesDetail(int movielID) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movielID)).build();
        }

        public static String getMovieIDIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
