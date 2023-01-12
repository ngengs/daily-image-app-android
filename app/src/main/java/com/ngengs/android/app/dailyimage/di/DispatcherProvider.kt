package com.ngengs.android.app.dailyimage.di

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface DispatcherProvider {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
}