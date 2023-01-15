package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.helpers.FakeDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutineModule::class]
)
class FakeCoroutineModule {
    private val dispatcher = UnconfinedTestDispatcher()

    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider = FakeDispatcherProvider(dispatcher)
}