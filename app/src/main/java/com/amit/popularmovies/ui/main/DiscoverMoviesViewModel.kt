package com.amit.popularmovies.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.amit.popularmovies.R
import com.amit.popularmovies.api.MoviesApi
import com.amit.popularmovies.base.BaseViewModel
import com.amit.popularmovies.model.MovieDiscoverResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DiscoverMoviesViewModel : BaseViewModel() {
    @Inject
    lateinit var moviesApi: MoviesApi

    private lateinit var subscription: Disposable
    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadMovies() }
    val moviesAdapter: MoviesAdapter = MoviesAdapter()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        subscription = moviesApi.getPopularMovies("a7028c1533e36a2a7e36ea6fd8e46bd6", "false",
                "popularity.desc", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrievePostListStart() }
                .doOnTerminate { onRetrievePostListFinish() }
                .subscribe(
                        { result -> onRetrievePostListSuccess(result!!.results) },
                        { onRetrievePostListError() }
                )
    }

    private fun onRetrievePostListStart(){
        loadingVisibility.value = View.VISIBLE
    }

    private fun onRetrievePostListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListSuccess(results: List<MovieDiscoverResult>) {
        moviesAdapter.updatePostList(results)
    }

    private fun onRetrievePostListError() {
        errorMessage.value = R.string.empty_movies_list
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}