package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.data.repository.implementation.FavoriteRepositoryImpl
import com.ngengs.android.app.dailyimage.data.repository.implementation.PhotoListRepositoryImpl
import com.ngengs.android.app.dailyimage.data.repository.implementation.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun bindPhotoListRepository(impl: PhotoListRepositoryImpl): PhotoListRepository

    @Binds
    @ViewModelScoped
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @ViewModelScoped
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}