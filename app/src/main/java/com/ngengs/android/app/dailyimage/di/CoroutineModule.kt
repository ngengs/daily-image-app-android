package com.ngengs.android.app.dailyimage.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineModule {
    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider = object : DispatcherProvider {
        override fun main() = Dispatchers.Main
        override fun io() = Dispatchers.IO
        override fun default() = Dispatchers.Default
    }
}