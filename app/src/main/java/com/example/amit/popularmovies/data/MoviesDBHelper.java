package com.example.amit.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amit.popularmovies.data.MoviesContract.MoviesEntry;

/**
* Created by amit on 7/20/2015.
*/
public class MoviesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_POSTERPATH + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_MOVID + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_RELEASEDATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_USERRATING + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_FAVOURITE + " INTEGER NOT NULL DEFAULT '0', " +
                " UNIQUE (" + MoviesEntry.COLUMN_MOVIE_MOVID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
