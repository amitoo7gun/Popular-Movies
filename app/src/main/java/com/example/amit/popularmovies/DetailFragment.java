package com.example.amit.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.amit.popularmovies.model.MovieDiscoverResult;
import com.example.amit.popularmovies.reviews.FetchReviewTask;
import com.example.amit.popularmovies.reviews.MovieReviewModel;
import com.example.amit.popularmovies.reviews.ReviewAdapter;
import com.example.amit.popularmovies.trailers.FetchTrailerTask;
import com.example.amit.popularmovies.trailers.MovieTrailerModel;
import com.example.amit.popularmovies.trailers.TrailerAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = DetailFragment.class.getSimpleName();
    static final String MOVIE_DATA = "MOVIE_DATA";

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";

    private MovieDiscoverResult movieDiscoverResult;
    private String mMoviesShare;
    private Uri mUri;
    private int favCheck;

    ////
    private boolean mHasData;
    private Intent intent;
    private ArrayList<MovieReviewModel> mReviewData;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    @BindView(R.id.review_list)
    LinearLayout mLinearLayoutReview;
    @BindView(R.id.trailer_list)
    LinearLayout mLinearLayoutTrailer;
    private String youtubeId;

    private String mMovieTitle;
    private String mMovieReleaseDate;


    @BindView(R.id.detail_movie_name_textview)
    TextView mMovieNameView;
    @BindView(R.id.movie_poster)
    ImageView mPosterView;
    @BindView(R.id.detail_movie_release_date_textview)
    TextView mReleaseDateView;
    @BindView(R.id.detail_movie_rating_textview)
    TextView mRatingView;
    @BindView(R.id.movie_favourite)
    ImageView mFavouriteView;
    @BindView(R.id.detail_movie_plot_textview)
    TextView mPlotView;
    @BindView(R.id.detail_movie_id_textview)
    TextView mMovieIdView;
    @BindView(R.id.toolbar)
    Toolbar toolbarView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
        movieDiscoverResult = intent.getParcelableExtra(MOVIE_DATA);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mHasData = true;
            FetchMovieElements(movieDiscoverResult.getId().toString());
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mHasData = true;
                FetchMovieElements(movieDiscoverResult.getId().toString());
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
            mUri = arguments.getParcelable(DetailFragment.MOVIE_DATA);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        ButterKnife.bind(this, rootView);

        mFavouriteView.setOnClickListener(this);

        initView(movieDiscoverResult);

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
        if (getActivity() instanceof DetailActivity) {
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
                getContext().getString(R.string.youtube_base_url) + youtubeId);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMoviesShare + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    public void initView(MovieDiscoverResult movieDiscoverResult) {
        if (movieDiscoverResult != null) {

            mMovieTitle = movieDiscoverResult.getTitle();
            mMovieReleaseDate = movieDiscoverResult.getReleaseDate();
            String poster_base_url = "http://image.tmdb.org/t/p/w500";
            String posterPath = poster_base_url + movieDiscoverResult.getPosterPath();
            Picasso.with(getContext())
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mPosterView);
            favCheck = 0;
            mMovieNameView.setText(mMovieTitle);
            mReleaseDateView.setText(getString(R.string.releasing_on_text) + " " + mMovieReleaseDate);
            mRatingView.setText(movieDiscoverResult.getVoteAverage().toString());
            mPlotView.setText(movieDiscoverResult.getOverview());
//            mMovieIdView.setText(movieDiscoverResult.getId());
            if (favCheck == 0) {
                mFavouriteView.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            } else
                mFavouriteView.setImageResource(R.drawable.ic_favorite_white_24dp);
            // Code for sharing option

        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        // We need to start the enter transition after the data has loaded
        if (activity instanceof DetailActivity) {
            activity.supportStartPostponedEnterTransition();

            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if (null != toolbarView) {
                Menu menu = toolbarView.getMenu();
                if (null != menu) menu.clear();
                toolbarView.inflateMenu(R.menu.detailfragment);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mFavouriteView.getId()) {
            ContentValues updateValues = new ContentValues();
            String whereCl = MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID + "= \"" + mMovieIdView.getText() + "\"";
            if (favCheck == 0) {

                updateValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE, "1");
                getContext().getContentResolver().update(MoviesContract.MoviesEntry.CONTENT_URI, updateValues, whereCl, null);
                mFavouriteView.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
                favCheck = 1;
            } else if (favCheck == 1) {
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
        if (Utility.isNetworkAvailable(getActivity())) {
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
                if (mTrailerAdapter.getCount() > 0) {
                    youtubeId = mTrailerAdapter.getItem(0).mKey;
                    if (menuItem != null)
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