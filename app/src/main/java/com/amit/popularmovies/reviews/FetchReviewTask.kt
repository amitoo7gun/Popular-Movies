package com.amit.popularmovies.reviews

import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.amit.popularmovies.DataBus.AsyncTaskResultEvent
import com.amit.popularmovies.DataBus.BusProvider
import com.amit.popularmovies.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

//import com.app.movie.cinephilia.OnReviewDataFetchFinished;
class FetchReviewTask //, OnReviewDataFetchFinished onReviewDataFetchFinished){
//this.onReviewDataFetchFinished = onReviewDataFetchFinished;
(var mContext: FragmentActivity?, var mReviewsAdapter: ReviewAdapter?) : AsyncTask<String?, Void?, ArrayList<MovieReviewModel>?>() {

    //OnReviewDataFetchFinished onReviewDataFetchFinished;
    private var progress: ProgressDialog? = null
    private val LOG_TAG = FetchReviewTask::class.java.simpleName
    override fun onPreExecute() {
        progress = ProgressDialog(mContext)
        progress!!.setMessage("Loading Data")
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress!!.isIndeterminate = true
        progress!!.setCancelable(false)
        progress!!.show()
    }


    override fun doInBackground(vararg params: String?): ArrayList<MovieReviewModel>? {
        val movieId: String?

        // If there's no sortby param
        if (params.size == 0) {
            return null
        }
        movieId = params[0]

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        // Will contain the raw JSON response as a string.
        var trailersJsonStr: String? = null
        try {
            // Construct the URL for the API
            val MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/"
            val API_KEY_PARAM = "api_key"
            val APPEND_PATH = "reviews" //params[1];
            val builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(APPEND_PATH)
                    .appendQueryParameter(API_KEY_PARAM, mContext?.getString(R.string.api_key))
                    .build()
            val url = URL(builtUri.toString())
            Log.v(LOG_TAG, "Built URI $builtUri")

            // Create the request and open the connection
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection!!.connect()

            // Read the input stream into a String
            val inputStream = urlConnection.inputStream
            val buffer = StringBuffer()
            if (inputStream == null) {
                // Nothing to do.
                return null
            }
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append("""$line""".trimIndent())
            }
            if (buffer.length == 0) {
                // Stream was empty.  No point in parsing.
                return null
            }
            trailersJsonStr = buffer.toString()
            Log.v(LOG_TAG, "Reviews string: $trailersJsonStr")
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error ", e)
            // If the code didn't successfully get the movies data, there's no point in attemping
            // to parse it.
            return null
        } finally {
            urlConnection?.disconnect()
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Error closing stream", e)
                }
            }
        }
        try {
            return getReviewsDataFromJson(trailersJsonStr)
        } catch (e: JSONException) {
            Log.e(LOG_TAG, e.message, e)
            e.printStackTrace()
        }

        // This will only happen if there was an error getting or parsing the trailers Data.
        return null
    }

    @Throws(JSONException::class)
    private fun getReviewsDataFromJson(reviewsJsonStr: String?): ArrayList<MovieReviewModel> {

        // Define json paths
        val REVIEW_AUTHOR = "author"
        val REVIEW_CONTENT = "content"
        val reviewJson = JSONObject(reviewsJsonStr)
        val reviewArray = reviewJson.getJSONArray("results")
        val reviews = ArrayList<MovieReviewModel>(reviewArray.length())
        for (i in 0 until reviewArray.length()) {
            var author: String
            var content: String

            // Get the JSON object representing the movie
            val reviewObject = reviewArray.getJSONObject(i)
            author = reviewObject.getString(REVIEW_AUTHOR)
            content = reviewObject.getString(REVIEW_CONTENT)
            reviews.add(MovieReviewModel(author, content))
        }
        for (movieReviewModel in reviews) {
            Log.v(LOG_TAG, "Review author: " + movieReviewModel.mAuthor)
            Log.v(LOG_TAG, "Review content: " + movieReviewModel.mContent)
        }
        return reviews
    }

    override fun onPostExecute(result: ArrayList<MovieReviewModel>?) {
        Log.v(LOG_TAG, "TASK POST EXECUTE")
        if (result != null) {
            mReviewsAdapter?.clear()
            for (elem in result) {
                mReviewsAdapter?.add(elem)
            }
        }
        //onReviewDataFetchFinished.reviewDataFetchFinished(true);
        BusProvider.getInstance().post(AsyncTaskResultEvent(true, "FetchReviewTask"))
        mReviewsAdapter?.notifyDataSetChanged()
        progress!!.dismiss()
    }

}