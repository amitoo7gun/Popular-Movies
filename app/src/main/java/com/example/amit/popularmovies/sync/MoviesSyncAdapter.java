package com.example.amit.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.example.amit.popularmovies.R;
import com.example.amit.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by amit on 7/23/2015.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL =1000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public static int items;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_SERVER_DOWN, STATUS_SERVER_INVALID, STATUS_UNKNOWN})
    public @interface ErrorStatus {}

    public static final int STATUS_OK = 0;
    public static final int STATUS_SERVER_DOWN = 1;
    public static final int STATUS_SERVER_INVALID = 2;
    public static final int STATUS_UNKNOWN = 3;




    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonStr = null;

        try {
            final String BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";

            final String QUERY_PARAM = "sort_by";
            final String API_KEY = "api_key";
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendQueryParameter(API_KEY, "a7028c1533e36a2a7e36ea6fd8e46bd6")
                    .appendQueryParameter(QUERY_PARAM, "popularity.desc");
            Uri builtUri = uriBuilder.build();
            Log.d("Updated URL",builtUri.toString());
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

           InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
            }
          reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                setErrorStatus(getContext(), STATUS_SERVER_DOWN);
            }
            JsonStr = buffer.toString();
            Log.v(LOG_TAG,JsonStr);
            getDataFromJson(JsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            setErrorStatus(getContext(), STATUS_SERVER_DOWN);
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setErrorStatus(getContext(), STATUS_SERVER_INVALID);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return;
    }

    private void getDataFromJson(String JsonStr)
            throws JSONException {

        final String MOVIE_TITLE = "title";
        final String MOVIE_POSTERPATH = "poster_path";
        final String MOVIE_ID = "id";
        final String MOVIE_RELEASEDATE = "release_date";
        final String MOVIE_USERRATING = "vote_average";
        final String MOVIE_PLOT = "overview";
        final String MOVIE_RESULTS = "results";


        try {
            JSONObject mov_data_Json = new JSONObject(JsonStr);
            Context context = getContext();

            String movie_title;
            String movie_posterpath;
            String movie_id;
            String movie_releasedate;
            String movie_rating;
            String movie_plot;


            JSONArray recordsArray = mov_data_Json.getJSONArray(MOVIE_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(recordsArray.length());


            for(int i = 0; i < recordsArray.length(); i++) {

                JSONObject movdata = recordsArray.getJSONObject(i);

                movie_title = movdata.getString(MOVIE_TITLE);
                movie_posterpath = movdata.getString(MOVIE_POSTERPATH);
                movie_id = movdata.getString(MOVIE_ID);
                movie_releasedate = movdata.getString(MOVIE_RELEASEDATE);
                movie_rating = movdata.getString(MOVIE_USERRATING);
                movie_plot = movdata.getString(MOVIE_PLOT);

                ContentValues moviesValues = new ContentValues();

                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, movie_title);
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTERPATH, movie_posterpath);
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID, movie_id);
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASEDATE, movie_releasedate);
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_USERRATING, movie_rating);
                moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_PLOT, movie_plot);


                cVVector.add(moviesValues);
            }
            if ( cVVector.size() > 0 ) {

                // delete old data
                getContext().getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                        null,
                        null);

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);

            }
            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    static private void setErrorStatus(Context c, @ErrorStatus int errorStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_error_status_key), errorStatus);
        spe.commit();
    }
}
