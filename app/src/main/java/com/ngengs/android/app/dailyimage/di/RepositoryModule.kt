package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.data.repository.implementation.PhotoListRepositoryImpl
import com.ngengs.android.app.dailyimage.data.repository.implementation.SearchRepositoryImpl
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(ViewModelComponent::class)
internal object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun providePhotoListRepository(
        localDataSource: PhotoLocalDataSource,
        remoteDataSource: PhotoRemoteDataSource,
        dispatcherProvider: DispatcherProvider,
    ): PhotoListRepository =
        PhotoListRepositoryImpl(localDataSource, remoteDataSource, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideSearchRepository(
        remoteDataSource: PhotoRemoteDataSource,
        dispatcherProvider: DispatcherProvider,
    ): SearchRepository =
        SearchRepositoryImpl(remoteDataSource, dispatcherProvider)
}