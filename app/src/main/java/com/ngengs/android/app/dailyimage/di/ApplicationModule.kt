package com.ngengs.android.app.dailyimage.di

import android.content.Context
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase
import com.ngengs.android.app.dailyimage.data.remote.UnsplashAPI
import com.ngengs.android.app.dailyimage.data.remote.UnsplashPublicAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by rizky.kharisma on 25/01/23.
 * @ngengs
 */

@Module
@InstallIn(SingletonComponent::class)
internal object ApplicationModule {
    @Singleton
    @Provides
    fun provideAPI(
        @ApplicationContext context: Context,
    ): UnsplashAPI = UnsplashAPI.instantiate(context)

    @Singleton
    @Provides
    fun providePublicAPI(
        @ApplicationContext context: Context,
    ): UnsplashPublicAPI = UnsplashPublicAPI.instantiate(context)

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DailyImageDatabase = DailyImageDatabase.initialize(context)
}