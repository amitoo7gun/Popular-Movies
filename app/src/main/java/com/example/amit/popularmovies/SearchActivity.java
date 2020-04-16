package com.example.amit.popularmovies;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.amit.popularmovies.data.MoviesContract;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private final static String SCREEN_LABEL = "Search";
    private SimpleCursorAdapter adapter;
    private int mPosition = ListView.INVALID_POSITION;


    SearchView mSearchView = null;
    String mQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQuery = query;


        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }

        overridePendingTransition(0, 0);

        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }
    private void handleIntent() {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {

            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        view.clearFocus();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                          fillData(s);
                        return true;
                    }
                });
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }

            if (!TextUtils.isEmpty(mQuery)) {
                view.setQuery(mQuery, false);
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MoviesContract.MoviesEntry._ID,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTERPATH };
        CursorLoader cursorLoader = new CursorLoader(this,
                MoviesContract.MoviesEntry.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void fillData(String q){
        ListView search_result_items =(ListView)findViewById(R.id.moviesearch_listView2);
        String[] COLUMNS_TO_BE_BOUND  = new String[] {
                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE
        };

        int[] LAYOUT_ITEMS_TO_FILL = new int[] {
                android.R.id.text1
        };
        Uri search_db_query = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().build();
        String[] whereArgs = new String[] {
                q
        };
        getLoaderManager().initLoader(0, null, this);
        Cursor cursor=getContentResolver().query(search_db_query, null, "title LIKE '%"+q+"%'", null, null);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                COLUMNS_TO_BE_BOUND,
                LAYOUT_ITEMS_TO_FILL,
                0);
        search_result_items.setAdapter(adapter);

        search_result_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class)
                        .setData(MoviesContract.MoviesEntry.buildMoviesDetail(
                                cursor.getInt(0)));
                startActivity(intent);
                mPosition = position;
            }
        });
    }
}
