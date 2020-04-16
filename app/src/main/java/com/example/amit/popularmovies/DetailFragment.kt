package com.example.amit.popularmovies

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.amit.popularmovies.DataBus.AsyncTaskResultEvent
import com.example.amit.popularmovies.DataBus.BusProvider
import com.example.amit.popularmovies.data.MoviesContract
import com.example.amit.popularmovies.model.MovieDiscoverResult
import com.example.amit.popularmovies.reviews.FetchReviewTask
import com.example.amit.popularmovies.reviews.MovieReviewModel
import com.example.amit.popularmovies.reviews.ReviewAdapter
import com.example.amit.popularmovies.trailers.FetchTrailerTask
import com.example.amit.popularmovies.trailers.TrailerAdapter
import com.squareup.otto.Subscribe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail_contents.*
import java.util.*

class DetailFragment : Fragment(), View.OnClickListener {
    private var movieDiscoverResult: MovieDiscoverResult? = null
    private var mMoviesShare: String? = null
    private var favCheck = 0

    ////
    private var mHasData = false
    private var intent: Intent? = null
    private var mReviewAdapter: ReviewAdapter? = null
    private var mTrailerAdapter: TrailerAdapter? = null

    private var youtubeId: String? = null
    private var mMovieTitle: String? = null
    private var mMovieReleaseDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent = activity!!.intent
        movieDiscoverResult = intent?.getParcelableExtra(MOVIE_DATA)
        if (intent != null && intent!!.hasExtra(Intent.EXTRA_TEXT)) {
            mHasData = true
            FetchMovieElements(movieDiscoverResult?.id.toString())
        } else {
            val arguments = arguments
            if (arguments != null) {
                mHasData = true
                FetchMovieElements(movieDiscoverResult?.id.toString())
            }
        }
    }

    override fun onResume() {
        Log.v(TAG, "On Resume")
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        Log.v(TAG, "On Pause")
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val arguments = arguments
//        if (arguments != null) {
//            movieDiscoverResult = arguments.getParcelable(MOVIE_DATA)
//        }
        val rootView = inflater.inflate(R.layout.fragment_detail_start, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView(movieDiscoverResult)
        mFavouriteView!!.setOnClickListener(this)
    }

    var menuItem: MenuItem? = null
    private fun finishCreatingMenu(menu: Menu) {
        menuItem = menu.findItem(R.id.action_share)
        //        menuItem.setIntent(createShareMovieIntent());
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (activity is DetailActivity) {
            inflater.inflate(R.menu.detailfragment, menu)
            menuItem = menu.findItem(R.id.action_share)
            //            finishCreatingMenu(menu);
        }
    }

    private fun createShareMovieIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        shareIntent.type = "text/plain"
        mMoviesShare = String.format("Hey! Watch %s releasing on %s,must watch trailer %s", mMovieTitle,
                mMovieReleaseDate,
                context!!.getString(R.string.youtube_base_url) + youtubeId)
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMoviesShare + MOVIE_SHARE_HASHTAG)
        return shareIntent
    }

    fun initView(movieDiscoverResult: MovieDiscoverResult?) {
        if (movieDiscoverResult != null) {
            mMovieTitle = movieDiscoverResult.title
            mMovieReleaseDate = movieDiscoverResult.release_date
            val poster_base_url = "http://image.tmdb.org/t/p/w500"
            val posterPath = poster_base_url + movieDiscoverResult.poster_path
            Picasso.with(context)
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mPosterView)
            favCheck = 0
            mMovieNameView!!.text = mMovieTitle
            mReleaseDateView!!.text = getString(R.string.releasing_on_text) + " " + mMovieReleaseDate
            mRatingView!!.text = movieDiscoverResult.vote_average.toString()
            mPlotView!!.text = movieDiscoverResult.overview
            //            mMovieIdView.setText(movieDiscoverResult.getId());
            if (favCheck == 0) {
                mFavouriteView!!.setImageResource(R.drawable.ic_favorite_border_white_24dp)
            } else mFavouriteView!!.setImageResource(R.drawable.ic_favorite_white_24dp)
            // Code for sharing option
        }
        val activity = activity as AppCompatActivity?
        // We need to start the enter transition after the data has loaded
        if (activity is DetailActivity) {
            activity.supportStartPostponedEnterTransition()
            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView as Toolbar?)
                activity.getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
                activity.getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            }
        } else {
            if (null != toolbarView) {
//                val menu = toolbarView!!.menu
//                menu?.clear()
//                toolbarView!!.inflateMenu(R.menu.detailfragment)
//                finishCreatingMenu(toolbarView!!.menu)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == mFavouriteView!!.id) {
            val updateValues = ContentValues()
            val whereCl = MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID + "= \"" + mMovieIdView!!.text + "\""
            if (favCheck == 0) {
                updateValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE, "1")
                context!!.contentResolver.update(MoviesContract.MoviesEntry.CONTENT_URI, updateValues, whereCl, null)
                mFavouriteView!!.setBackgroundResource(R.drawable.ic_favorite_white_24dp)
                favCheck = 1
            } else if (favCheck == 1) {
                updateValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVOURITE, "0")
                context!!.contentResolver.update(MoviesContract.MoviesEntry.CONTENT_URI, updateValues, whereCl, null)
                mFavouriteView!!.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp)
                favCheck = 0
            }
        }
    }

    fun FetchMovieElements(movieId: String?) {
        val mReviewData: ArrayList<MovieReviewModel>? = ArrayList()
        mReviewAdapter = ReviewAdapter(activity, R.layout.list_item_review, mReviewData)
        mTrailerAdapter = TrailerAdapter(activity, R.layout.list_item_trailer, ArrayList())
        if (Utility.isNetworkAvailable(activity)) {
            // Fetch Review data
            val reviewTask = FetchReviewTask(activity, mReviewAdapter)
            reviewTask.execute(movieId)

            // Fetch Trailer data
            val trailerTask = FetchTrailerTask(activity, mTrailerAdapter)
            trailerTask.execute(movieId)
        }
    }

    @Subscribe
    fun onMovieLoaded(event: AsyncTaskResultEvent) {
//        if (event.result) {
//            if (event.name == "FetchReviewTask") {
//                for (iter in 0 until mReviewAdapter!!.count) {
//                    val item = mReviewAdapter!!.getView(iter, null, null)
//                    mLinearLayoutReview!!.addView(item)
//                }
//            } else if (event.name == "FetchTrailerTask") {
//                if (mTrailerAdapter!!.count > 0) {
//                    youtubeId = mTrailerAdapter!!.getItem(0)!!.mKey
//                    if (menuItem != null) menuItem!!.intent = createShareMovieIntent()
//                    for (iter in 0 until mTrailerAdapter!!.count) {
//                        val item = mTrailerAdapter!!.getView(iter, null, null)
//                        mLinearLayoutTrailer!!.addView(item)
//                    }
//                }
//            }
//        }
    }

    companion object {
        private val TAG = DetailFragment::class.java.simpleName
        const val MOVIE_DATA = "MOVIE_DATA"
        private const val MOVIE_SHARE_HASHTAG = " #PopularMoviesApp"
    }

    init {
        setHasOptionsMenu(true)
    }
}