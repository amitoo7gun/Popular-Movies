package com.example.amit.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetTrailerTask extends AsyncTask<String,Void,String[]> {
    public final String LOG_TAG = GetTrailerTask.class.getSimpleName();

    public GetTrailerTask() {

    }

    GetTrailerKeysInteface getTrailerKeysInteface;

    public void setTrailerKeysListener(GetTrailerKeysInteface getTrailerKeysInteface) {
        this.getTrailerKeysInteface = getTrailerKeysInteface;
    }

    @Override
    protected String[] doInBackground(String... movieId) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;

            try {

                final String BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+ movieId[0] + "/videos?";
                Log.d("MOVIE ID",movieId[0]);
//                Log.d("MOVIE ID 2",movieId[0].toString());
                final String API_KEY = "api_key";
                Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
                uriBuilder.appendQueryParameter(API_KEY, "a7028c1533e36a2a7e36ea6fd8e46bd6");

                Uri builtUri = uriBuilder.build();
                Log.d("Updated URL",builtUri.toString());
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                //InputStream inputStream = getClass().getResourceAsStream("game_data.json");
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.

                }
                JsonStr = buffer.toString();
                return getDataFromJson(JsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
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

        return null;
    }



    private String[] getDataFromJson(String JsonStr)
            throws JSONException {
        Log.v(LOG_TAG,JsonStr);
//        String[] movie_trailer_key = new String[];
        final String MOVIE_RESULTS = "results";
        final String MOVIE_KEY = "key";
        try {
            JSONObject mov_data_Json = new JSONObject(JsonStr);


            JSONArray recordsArray = mov_data_Json.getJSONArray(MOVIE_RESULTS);

            String[] movie_trailer_key= new String[recordsArray.length()];

            for(int i = 0; i < recordsArray.length(); i++) {

                JSONObject movData = recordsArray.getJSONObject(i);

                movie_trailer_key[i] = movData.getString(MOVIE_KEY);

            }
//            for (String trailerkey:movie_trailer_key
//                 ) {
//                Log.d(LOG_TAG, "Trailer Key: " +trailerkey);
//
//            }
            Log.d(LOG_TAG, "Trailer length: " +movie_trailer_key.length);
            return movie_trailer_key;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] trailerKeys) {
        super.onPostExecute(trailerKeys);
        for (String trailerkey:trailerKeys
                ) {
            Log.d(LOG_TAG, "Trailer Key:: " +trailerkey);

        }
        getTrailerKeysInteface.getTrailerKeys(trailerKeys);
    }

    public static interface GetTrailerKeysInteface
    {
        void getTrailerKeys(String[] trailerKeys);
    }
}
