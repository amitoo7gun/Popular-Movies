package com.amit.popularmovies.base

import androidx.lifecycle.ViewModel
import com.amit.popularmovies.injection.component.DaggerViewModelInjector
import com.amit.popularmovies.injection.component.ViewModelInjector
import com.amit.popularmovies.injection.module.NetworkModule
import com.amit.popularmovies.ui.main.DiscoverMoviesViewModel

abstract class BaseViewModel: ViewModel(){
    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is DiscoverMoviesViewModel -> injector.inject(this)
        }
    }
}