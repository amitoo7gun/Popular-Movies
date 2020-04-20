package com.amit.popularmovies.injection.component

import com.amit.popularmovies.injection.module.NetworkModule
import com.amit.popularmovies.ui.main.DiscoverMoviesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(discoverMoviesViewModel: DiscoverMoviesViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}