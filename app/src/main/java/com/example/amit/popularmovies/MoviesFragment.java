package com.example.amit.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

import com.example.amit.popularmovies.api.MoviesService;
import com.example.amit.popularmovies.api.NetworkInstance;
import com.example.amit.popularmovies.data.MoviesContract;
import com.example.amit.popularmovies.model.DiscoverMovieResponse;
import com.example.amit.popularmovies.model.MovieDiscoverResult;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private MoviesAdapter mMoviesAdapter;
    private boolean mAutoSelectView;
    private int mChoiceMode;

    private RecyclerView mRecyclerView;
    private FloatingActionButton mSortbyPopular;
    private FloatingActionButton mSortbyRating;
    private FloatingActionButton mSortbyFavourite;


    public static SharedPreferences sp;
    int item_count = 0;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    // SQL query Pareameters
    public static String sortOrder = null;
    public static String whereClause = null;
    public static String whereArgs = null;


    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIES_COLUMNS = {

            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTERPATH,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID

    };

    static final int COL_MOVIES_ID = 0;
    static final int COL_MOVIES_TITLE = 1;
    static final int COL_MOVIES_POSTERPATH = 2;
    static final int COL_MOVIES_MOVIEID = 3;


    public interface ItemSelectCallback {

        public void onItemSelected(MovieDiscoverResult movieDiscoverResult);
    }

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerview_products);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (MainActivity.mTwoPane) ? 3 : 2));
        View emptyView = rootView.findViewById(R.id.recyclerview_movies_empty);
        mSortbyPopular = rootView.findViewById(R.id.sort_item_popular);
        mSortbyRating = rootView.findViewById(R.id.sort_item_high_rating);
        mSortbyFavourite = rootView.findViewById(R.id.sort_item_favourite);
        mSortbyPopular.setOnClickListener(this);
        mSortbyRating.setOnClickListener(this);
        mSortbyFavourite.setOnClickListener(this);

        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(getActivity(), new MoviesAdapter.MoviesAdapterOnClickHandler() {
            @Override
            public void onClick(MovieDiscoverResult movieDiscoverResult, MoviesAdapter.MoviesAdapterViewHolder vh) {
                ((ItemSelectCallback) getActivity())
                        .onItemSelected(movieDiscoverResult);
                mPosition = vh.getAdapterPosition();
            }
        }, emptyView, mChoiceMode);


        mRecyclerView.setAdapter(mMoviesAdapter);

        sp = this.getActivity().getSharedPreferences("service_validation", Context.MODE_PRIVATE);
        item_count = sp.getInt("TOTAL_ITEMS", item_count);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {

                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            mMoviesAdapter.onRestoreInstanceState(savedInstanceState);
        }

        fetchMoviesData();

        return rootView;
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.ForecastFragment,
                0, 0);
        mChoiceMode = a.getInt(R.styleable.ForecastFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        mAutoSelectView = a.getBoolean(R.styleable.ForecastFragment_autoSelectView, false);
        a.recycle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        mMoviesAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void fetchMoviesData() {
        MoviesService apiService =
                NetworkInstance.getRetrofitInstance().create(MoviesService.class);

        Call<DiscoverMovieResponse> call = apiService.getPopularMovies(getContext().getString(R.string.api_key), "false",
                "popularity.desc", "1");
        call.enqueue(new Callback<DiscoverMovieResponse>() {
            @Override
            public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
                int statusCode = response.code();
                DiscoverMovieResponse discoverMovieResponse = response.body();
                if(discoverMovieResponse != null)
                    updateView(discoverMovieResponse.getResults());
                else
                    updateEmptyView();
            }

            @Override
            public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
                // Log error here since request failed
                updateEmptyView();
            }
        });
    }

    private void updateView(List<MovieDiscoverResult> results) {
        mMoviesAdapter.setMoviesData(results);
        if (mPosition != RecyclerView.NO_POSITION) {

            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
        if (results.size() > 0) {
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int itemPosition = mMoviesAdapter.getSelectedItemPosition();
                        if (RecyclerView.NO_POSITION == itemPosition) itemPosition = 0;
                        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(itemPosition);
                        if (null != vh && mAutoSelectView) {
                            mMoviesAdapter.selectView(vh);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mSortbyFavourite.getId()) {
            whereClause = MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE + " =1";
            sortOrder = null;
        } else if (v.getId() == mSortbyRating.getId()) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_MOVIE_USERRATING + " DESC";
            whereClause = null;
        } else if (v.getId() == mSortbyPopular.getId()) {
            sortOrder = null;
            whereClause = null;
        }
    }

    private void updateEmptyView() {
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_error_status_key))) {
            updateEmptyView();
        }
    }
}
