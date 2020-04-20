package com.amit.popularmovies.ui.search

import android.app.LoaderManager
import android.app.SearchManager
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.amit.popularmovies.R
import com.amit.popularmovies.ui.detail.DetailActivity

class SearchActivity : AppCompatActivity(){
//    private var adapter: SimpleCursorAdapter? = null
//    private var mPosition = ListView.INVALID_POSITION
//    var mSearchView: SearchView? = null
//    var mQuery = ""
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_result)
//        val toolbar = findViewById<View>(R.id.toolbarView) as Toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        var query = intent.getStringExtra(SearchManager.QUERY)
//        query = query ?: ""
//        mQuery = query
//        if (mSearchView != null) {
//            mSearchView!!.setQuery(query, false)
//        }
//        overridePendingTransition(0, 0)
//        handleIntent()
//    }
//
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        handleIntent()
//    }
//
//    private fun handleIntent() {}
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.search, menu)
//        val searchItem = menu.findItem(R.id.menu_search)
//        if (searchItem != null) {
//            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//            val view = searchItem.actionView as SearchView
//            mSearchView = view
//            if (view == null) {
//            } else {
//                view.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//                view.isIconified = false
//                view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(s: String): Boolean {
//                        view.clearFocus()
//                        return true
//                    }
//
//                    override fun onQueryTextChange(s: String): Boolean {
//                        fillData(s)
//                        return true
//                    }
//                })
//                view.setOnCloseListener {
//                    finish()
//                    false
//                }
//            }
//            if (!TextUtils.isEmpty(mQuery)) {
//                view.setQuery(mQuery, false)
//            }
//        }
//        return true
//    }
//
//    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
//        adapter!!.swapCursor(data)
//    }
//
//    override fun onLoaderReset(loader: Loader<Cursor>) {
//        adapter!!.swapCursor(null)
//    }
//
//    private fun fillData(q: String) {
//        val search_result_items = findViewById<View>(R.id.moviesearch_listView2) as ListView
//        val COLUMNS_TO_BE_BOUND = arrayOf(
//                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE
//        )
//        val LAYOUT_ITEMS_TO_FILL = intArrayOf(
//                android.R.id.text1
//        )
//        val search_db_query = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().build()
//        val whereArgs = arrayOf(
//                q
//        )
//        loaderManager.initLoader(0, null, this)
//        val cursor = contentResolver.query(search_db_query, null, "title LIKE '%$q%'", null, null)
//        adapter = SimpleCursorAdapter(this,
//                android.R.layout.simple_list_item_1,
//                cursor,
//                COLUMNS_TO_BE_BOUND,
//                LAYOUT_ITEMS_TO_FILL,
//                0)
//        search_result_items.adapter = adapter
//        search_result_items.onItemClickListener = OnItemClickListener { adapterView, view, position, l ->
//            val cursor = adapterView.getItemAtPosition(position) as Cursor
//            val intent = Intent(this@SearchActivity, DetailActivity::class.java)
//                    .setData(MoviesContract.MoviesEntry.buildMoviesDetail(
//                            cursor.getInt(0)))
//            startActivity(intent)
//            mPosition = position
//        }
//    }
//
//    companion object {
//        private const val SCREEN_LABEL = "Search"
//    }
}