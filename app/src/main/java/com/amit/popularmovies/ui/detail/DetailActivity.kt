package com.amit.popularmovies.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amit.popularmovies.R
import com.amit.popularmovies.model.MovieDiscoverResult

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        if (savedInstanceState == null) {
            val arguments = Bundle()
            arguments.putParcelable(DetailFragment.MOVIE_DATA, intent.getParcelableExtra<MovieDiscoverResult>(DetailFragment.MOVIE_DATA))
            val fragment = DetailFragment()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                    .add(R.id.product_detail_container, fragment)
                    .commit()
        }
    }
}