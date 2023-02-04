package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.data.source.implementation.PhotoLocalDataSourceImpl
import com.ngengs.android.app.dailyimage.data.source.implementation.PhotoRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun providePhotoRemoteDataSource(
        impl: PhotoRemoteDataSourceImpl,
    ): PhotoRemoteDataSource

    @Singleton
    @Binds
    abstract fun providePhotoLocalDataSource(
        impl: PhotoLocalDataSourceImpl,
    ): PhotoLocalDataSource
}