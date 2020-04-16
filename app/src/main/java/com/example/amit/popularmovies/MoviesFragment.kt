package com.example.amit.popularmovies

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amit.popularmovies.MoviesAdapter.MoviesAdapterOnClickHandler
import com.example.amit.popularmovies.api.MoviesService
import com.example.amit.popularmovies.api.NetworkInstance
import com.example.amit.popularmovies.data.MoviesContract
import com.example.amit.popularmovies.model.DiscoverMovieResponse
import com.example.amit.popularmovies.model.MovieDiscoverResult
import com.github.clans.fab.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesFragment : Fragment(), View.OnClickListener, OnSharedPreferenceChangeListener {
    private var mMoviesAdapter: MoviesAdapter? = null
    private var mAutoSelectView = false
    private var mChoiceMode = 0
    private var mRecyclerView: RecyclerView? = null
    private var mSortbyPopular: FloatingActionButton? = null
    private var mSortbyRating: FloatingActionButton? = null
    private var mSortbyFavourite: FloatingActionButton? = null
    var item_count = 0
    private var mPosition = RecyclerView.NO_POSITION

    interface ItemSelectCallback {
        fun onItemSelected(movieDiscoverResult: MovieDiscoverResult?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movies_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        mRecyclerView = rootView.findViewById(R.id.recyclerview_products)
        mRecyclerView?.setLayoutManager(GridLayoutManager(activity, if (MainActivity.mTwoPane) 3 else 2))
        val emptyView = rootView.findViewById<View>(R.id.recyclerview_movies_empty)
        mSortbyPopular = rootView.findViewById(R.id.sort_item_popular)
        mSortbyRating = rootView.findViewById(R.id.sort_item_high_rating)
        mSortbyFavourite = rootView.findViewById(R.id.sort_item_favourite)
        mSortbyPopular?.setOnClickListener(this)
        mSortbyRating?.setOnClickListener(this)
        mSortbyFavourite?.setOnClickListener(this)
        mRecyclerView?.setHasFixedSize(true)
        mMoviesAdapter = MoviesAdapter(activity, object : MoviesAdapterOnClickHandler {
            override fun onClick(movieDiscoverResult: MovieDiscoverResult?, vh: MoviesAdapter.MoviesAdapterViewHolder) {
                (activity as ItemSelectCallback?)
                        ?.onItemSelected(movieDiscoverResult)
                mPosition = vh.adapterPosition
            }
        }, emptyView, mChoiceMode)
        mRecyclerView?.setAdapter(mMoviesAdapter)
        sp = this.activity!!.getSharedPreferences("service_validation", Context.MODE_PRIVATE)
        item_count = sp?.getInt("TOTAL_ITEMS", item_count)!!
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTED_KEY)
            }
            mMoviesAdapter!!.onRestoreInstanceState(savedInstanceState)
        }
        fetchMoviesData()
        return rootView
    }

    override fun onInflate(activity: Activity, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(activity, attrs, savedInstanceState)
        val a = activity.obtainStyledAttributes(attrs, R.styleable.ForecastFragment,
                0, 0)
        mChoiceMode = a.getInt(R.styleable.ForecastFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE)
        mAutoSelectView = a.getBoolean(R.styleable.ForecastFragment_autoSelectView, false)
        a.recycle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition)
        }
        mMoviesAdapter!!.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun fetchMoviesData() {
        val apiService = NetworkInstance.retrofitInstance?.create(MoviesService::class.java)
        val call = apiService?.getPopularMovies(context!!.getString(R.string.api_key), "false",
                "popularity.desc", "1")
        call?.enqueue(object : Callback<DiscoverMovieResponse?> {
            override fun onResponse(call: Call<DiscoverMovieResponse?>, response: Response<DiscoverMovieResponse?>) {
                val statusCode = response.code()
                val discoverMovieResponse = response.body()
                if (discoverMovieResponse != null) updateView(discoverMovieResponse.results) else updateEmptyView()
            }

            override fun onFailure(call: Call<DiscoverMovieResponse?>, t: Throwable) {
                // Log error here since request failed
                updateEmptyView()
            }
        })
    }

    private fun updateView(results: List<MovieDiscoverResult>?) {
        mMoviesAdapter!!.setMoviesData(results)
        if (mPosition != RecyclerView.NO_POSITION) {
            mRecyclerView!!.smoothScrollToPosition(mPosition)
        }
        updateEmptyView()
        if (results?.size!! > 0) {
            mRecyclerView!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView!!.childCount > 0) {
                        mRecyclerView!!.viewTreeObserver.removeOnPreDrawListener(this)
                        var itemPosition = mMoviesAdapter?.selectedItemPosition
                        if (RecyclerView.NO_POSITION == itemPosition) itemPosition = 0
                        val vh = itemPosition?.let { mRecyclerView!!.findViewHolderForAdapterPosition(it) }
                        if (null != vh && mAutoSelectView) {
                            mMoviesAdapter!!.selectView(vh)
                        }
                        return true
                    }
                    return false
                }
            })
        }
    }

    override fun onClick(v: View) {
        if (v.id == mSortbyFavourite!!.id) {
            whereClause = MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE + " =1"
            sortOrder = null
        } else if (v.id == mSortbyRating!!.id) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_MOVIE_USERRATING + " DESC"
            whereClause = null
        } else if (v.id == mSortbyPopular!!.id) {
            sortOrder = null
            whereClause = null
        }
    }

    private fun updateEmptyView() {
//        if (mMoviesAdapter.getItemCount() == 0) {
//            TextView tv = (TextView) getView().findViewById(R.id.recyclerview_movies_empty);
//            if (null != tv) {
//                // if cursor is empty, why? do we have an invalid location
//                int message = R.string.empty_forecast_list;
//                @MoviesSyncAdapter.ErrorStatus int location = Utility.getErrorStatus(getActivity());
//                switch (location) {
//                    case MoviesSyncAdapter.STATUS_SERVER_DOWN:
//                        message = R.string.empty_movie_list_server_down;
//                        break;
//                    case MoviesSyncAdapter.STATUS_SERVER_INVALID:
//                        message = R.string.empty_movie_list_server_error;
//                        break;
//                    default:
//                        if (!Utility.isNetworkAvailable(getActivity())) {
//                            message = R.string.empty_movie_list_no_network;
//                        }
//                }
//                tv.setText(message);
//            }
//        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.pref_error_status_key)) {
            updateEmptyView()
        }
    }

    companion object {
        val LOG_TAG = MoviesFragment::class.java.simpleName
        var sp: SharedPreferences? = null
        private const val SELECTED_KEY = "selected_position"

        // SQL query Pareameters
        var sortOrder: String? = null
        var whereClause: String? = null
        var whereArgs: String? = null
        private const val MOVIES_LOADER = 0
        private val MOVIES_COLUMNS = arrayOf(
                MoviesContract.MoviesEntry._ID,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTERPATH,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID
        )
        const val COL_MOVIES_ID = 0
        const val COL_MOVIES_TITLE = 1
        const val COL_MOVIES_POSTERPATH = 2
        const val COL_MOVIES_MOVIEID = 3
    }
}