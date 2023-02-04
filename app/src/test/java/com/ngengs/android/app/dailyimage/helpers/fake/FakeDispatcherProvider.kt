package com.ngengs.android.app.dailyimage.helpers.fake

import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeDispatcherProvider(
    private val testDispatcher: TestDispatcher,
) : DispatcherProvider {
    override fun main(): CoroutineDispatcher = testDispatcher
    override fun io(): CoroutineDispatcher = testDispatcher
    override fun default(): CoroutineDispatcher = testDispatcher
}