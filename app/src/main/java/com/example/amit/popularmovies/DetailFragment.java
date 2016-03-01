package com.example.amit.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amit.popularmovies.DataBus.AsyncTaskResultEvent;
import com.example.amit.popularmovies.DataBus.BusProvider;
import com.example.amit.popularmovies.data.MoviesContract;
import com.example.amit.popularmovies.reviews.FetchReviewTask;
import com.example.amit.popularmovies.reviews.MovieReviewModel;
import com.example.amit.popularmovies.reviews.ReviewAdapter;
import com.example.amit.popularmovies.trailers.FetchTrailerTask;
import com.example.amit.popularmovies.trailers.MovieTrailerModel;
import com.example.amit.popularmovies.trailers.TrailerAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener {
    private static final String TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";

    private String mMoviesShare;
    private Uri mUri;
    private int favCheck;

    ////
    private boolean mHasData;
    private Intent intent;
    private ArrayList<MovieReviewModel> mReviewData;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    @Bind(R.id.review_list) LinearLayout mLinearLayoutReview;
    @Bind(R.id.trailer_list) LinearLayout mLinearLayoutTrailer;
    private String youtubeId;


    // Public members
    public static final String ARG_MOVIE = "movieFragment";


    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTERPATH,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASEDATE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_USERRATING,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_PLOT,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE
    };

    public static final int COL_MOVIES_TITLE = 0;
    public static final int COL_MOVIES_POSTERPATH = 1;
    public static final int COL_MOVIES_ID = 2;
    public static final int COL_MOVIES_RELEASEDATE = 3;
    public static final int COL_MOVIES_USERRATING = 4;
    public static final int COL_MOVIES_PLOT = 5;
    public static final int COL_MOVIES_FAVOURITE = 6;

    public String mMovieTitle;
    public String mMovieReleaseDate;


    @Bind(R.id.detail_movie_name_textview) TextView mMovieNameView;
    @Bind(R.id.movie_poster) ImageView mPosterView;
    @Bind(R.id.detail_movie_release_date_textview) TextView mReleaseDateView;
    @Bind(R.id.detail_movie_rating_textview) TextView mRatingView;
    @Bind(R.id.movie_favourite) ImageView mFavouriteView;
    @Bind(R.id.detail_movie_plot_textview) TextView mPlotView;
    @Bind(R.id.detail_movie_id_textview) TextView mMovieIdView;


    //
//    public static DetailFragment newInstance() {
//        DetailFragment fragment = new DetailFragment();
//        return fragment;
//    }
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mHasData = true;
//            Log.d("Movie Id received:",MainActivity.movieId);
            FetchMovieElements(MainActivity.movieId);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mHasData = true;
                Log.v(TAG, "Arguments !null");
//                Log.d("Movie Id received:",MainActivity.movieId);
                FetchMovieElements(MainActivity.movieId);
            }
        }
    }


    @Override
    public void onResume() {
        Log.v(TAG, "On Resume");
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.v(TAG, "On Pause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        ButterKnife.bind(this, rootView);

        mFavouriteView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
    public MenuItem menuItem;
    private void finishCreatingMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_share);
//        menuItem.setIntent(createShareMovieIntent());
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if ( getActivity() instanceof DetailActivity ){
            inflater.inflate(R.menu.detailfragment, menu);
            menuItem = menu.findItem(R.id.action_share);
//            finishCreatingMenu(menu);
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        mMoviesShare = String.format("Hey! Watch %s releasing on %s,must watch trailer %s", mMovieTitle,
                mMovieReleaseDate,
                getContext().getString(R.string.youtube_base_url)+youtubeId);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMoviesShare + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            mMovieTitle = data.getString(COL_MOVIES_TITLE);
            mMovieReleaseDate = data.getString(COL_MOVIES_RELEASEDATE);
            String poster_base_url = "http://image.tmdb.org/t/p/w500";
            String posterPath =poster_base_url + data.getString(COL_MOVIES_POSTERPATH);
            Picasso.with(getContext())
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mPosterView);
            favCheck = data.getInt(COL_MOVIES_FAVOURITE);
            mMovieNameView.setText(mMovieTitle);
            mReleaseDateView.setText(getString(R.string.releasing_on_text) + " " + mMovieReleaseDate);
            mRatingView.setText(data.getString(COL_MOVIES_USERRATING));
            mPlotView.setText(data.getString(COL_MOVIES_PLOT));
            mMovieIdView.setText(data.getString(COL_MOVIES_ID));
            if(favCheck == 0) {
                mFavouriteView.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }
            else
                mFavouriteView.setImageResource(R.drawable.ic_favorite_white_24dp);
            // Code for sharing option

        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);
        // We need to start the enter transition after the data has loaded
        if (activity instanceof DetailActivity) {
            activity.supportStartPostponedEnterTransition();

            if ( null != toolbarView ) {
                activity.setSupportActionBar(toolbarView);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if ( null != toolbarView ) {
                Menu menu = toolbarView.getMenu();
                if ( null != menu ) menu.clear();
                toolbarView.inflateMenu(R.menu.detailfragment);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onClick(View v) {

        if(v.getId() == mFavouriteView.getId())
        {
            ContentValues updateValues = new ContentValues();
            String whereCl = MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID + "= \"" + mMovieIdView.getText() + "\"";
            if(favCheck == 0) {

                updateValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE, "1");
                getContext().getContentResolver().update(MoviesContract.MoviesEntry.CONTENT_URI, updateValues, whereCl, null);
                mFavouriteView.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
                favCheck = 1;
            }
            else if(favCheck == 1)
            {
                updateValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE, "0");
                getContext().getContentResolver().update(MoviesContract.MoviesEntry.CONTENT_URI, updateValues, whereCl, null);
                mFavouriteView.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
                favCheck = 0;
            }
        }
    }


    public void FetchMovieElements(String movieId) {
        mReviewData = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, mReviewData);
        mTrailerAdapter = new TrailerAdapter(getActivity(), R.layout.list_item_trailer, new ArrayList<MovieTrailerModel>());
        if(Utility.isNetworkAvailable(getActivity())) {
            // Fetch Review data
            FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), mReviewAdapter);
            reviewTask.execute(movieId);

            // Fetch Trailer data
            FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity(), mTrailerAdapter);
            trailerTask.execute(movieId);
        }
    }


    @Subscribe
    public void onMovieLoaded(AsyncTaskResultEvent event) {
        if (event.getResult()) {
            if (event.getName().equals("FetchReviewTask")) {
                for (int iter = 0; iter < mReviewAdapter.getCount(); iter++) {
                    View item = mReviewAdapter.getView(iter, null, null);
                    mLinearLayoutReview.addView(item);
                }
            } else if (event.getName().equals("FetchTrailerTask")) {
                if(mTrailerAdapter.getCount()>0) {
                    youtubeId = mTrailerAdapter.getItem(0).mKey;
                    if(menuItem != null)
                    menuItem.setIntent(createShareMovieIntent());
                    for (int iter = 0; iter < mTrailerAdapter.getCount(); iter++) {
                        View item = mTrailerAdapter.getView(iter, null, null);
                        mLinearLayoutTrailer.addView(item);
                    }
                }
            }
        }
    }
}