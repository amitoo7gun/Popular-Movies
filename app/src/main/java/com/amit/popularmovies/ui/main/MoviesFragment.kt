package com.amit.popularmovies.ui.main

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amit.popularmovies.R
import com.amit.popularmovies.databinding.FragmentMoviesBinding
import com.amit.popularmovies.model.MovieDiscoverResult
import com.amit.popularmovies.ui.detail.DetailActivity
import com.amit.popularmovies.ui.detail.DetailFragment
import com.amit.popularmovies.ui.utils.MoviesNavigator
import com.github.clans.fab.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MoviesFragment : Fragment(), View.OnClickListener, OnSharedPreferenceChangeListener, MoviesNavigator {

    private lateinit var viewModel: DiscoverMoviesViewModel
    private lateinit var binding: FragmentMoviesBinding
    private var errorSnackbar: Snackbar? = null

    private var mAutoSelectView = false
    private var mChoiceMode = 0
    private var mRecyclerView: RecyclerView? = null
    private var mSortbyPopular: FloatingActionButton? = null
    private var mSortbyRating: FloatingActionButton? = null
    private var mSortbyFavourite: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movies_fragment, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = inflate(
                inflater, R.layout.fragment_movies, container, false)
        val rootView: View = binding.getRoot()

        viewModel = ViewModelProviders.of(this).get(DiscoverMoviesViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (errorMessage != null) showError(errorMessage) else hideError()
        })


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

        viewModel.setNavigator(this)

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

    override fun onClick(v: View) {
        if (v.id == mSortbyFavourite!!.id) {
//            whereClause = MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE + " =1"
//            sortOrder = null
        } else if (v.id == mSortbyRating!!.id) {
//            sortOrder = MoviesContract.MoviesEntry.COLUMN_MOVIE_USERRATING + " DESC"
//            whereClause = null
        } else if (v.id == mSortbyPopular!!.id) {
//            sortOrder = null
//            whereClause = null
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

    private fun showError(@StringRes errorMessage: Int) {
        errorSnackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, viewModel.errorClickListener)
        errorSnackbar?.show()
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }

    override fun onMovieItemClick(movieDiscoverResult: MovieDiscoverResult) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailFragment.MOVIE_DATA, movieDiscoverResult)
        activity?.startActivity(intent)
    }
}