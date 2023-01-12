package com.ngengs.android.app.dailyimage.di

import android.content.Context
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase
import com.ngengs.android.app.dailyimage.data.remote.UnsplashAPI
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.data.source.implementation.PhotoLocalDataSourceImpl
import com.ngengs.android.app.dailyimage.data.source.implementation.PhotoRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DataSourceModule {

    @Singleton
    @Provides
    fun providePhotoRemoteDataSource(
        githubAPI: UnsplashAPI,
        dispatcherProvider: DispatcherProvider,
    ): PhotoRemoteDataSource = PhotoRemoteDataSourceImpl(githubAPI, dispatcherProvider)

    @Singleton
    @Provides
    fun providePhotoLocalDataSource(
        database: DailyImageDatabase,
        dispatcherProvider: DispatcherProvider,
    ): PhotoLocalDataSource = PhotoLocalDataSourceImpl(database, dispatcherProvider)

    @Singleton
    @Provides
    fun provideAPI(
        @ApplicationContext context: Context
    ): UnsplashAPI = UnsplashAPI.instantiate(context)

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DailyImageDatabase = DailyImageDatabase.initialize(context)
}