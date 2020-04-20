package com.amit.popularmovies.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.amit.popularmovies.R
import com.amit.popularmovies.model.MovieDiscoverResult
import com.amit.popularmovies.ui.detail.DetailActivity
import com.amit.popularmovies.ui.detail.DetailFragment
import com.amit.popularmovies.ui.main.MoviesFragment.ItemSelectCallback
import com.amit.popularmovies.ui.search.SearchActivity

class MainActivity : AppCompatActivity(), ItemSelectCallback {
    private val LOG_TAG = MainActivity::class.java.simpleName
    private var mLayout: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbarView) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mLayout = findViewById(R.id.sample_main_layout)
        if (findViewById<View?>(R.id.movies_detail_container) != null) {
            mTwoPane = true
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.movies_detail_container, DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit()
            }
        } else {
            mTwoPane = false
            supportActionBar!!.elevation = 0f
        }
        val moviesFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_movies) as MoviesFragment?
        //        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(movieDiscoverResult: MovieDiscoverResult?) {
        if (mTwoPane) {
            val args = Bundle()
            args.putParcelable(DetailFragment.MOVIE_DATA, movieDiscoverResult)
            val fragment = DetailFragment()
            fragment.arguments = args
            supportFragmentManager.beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit()
        } else {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailFragment.MOVIE_DATA, movieDiscoverResult)
            startActivity(intent)
        }
    }

    companion object {
        private const val DETAILFRAGMENT_TAG = "DFTAG"

        @JvmField
        var mTwoPane = false
    }
}