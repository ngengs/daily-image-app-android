package com.ngengs.android.app.dailyimage.presenter

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidTest
class DailyImageApplicationTest {
    private lateinit var application: DailyImageApplication

    @Test
    fun test_onCreate() {
        // When
        application = DailyImageApplication()
        application.onCreate()

        // Then
        assertThat(Timber.treeCount).isEqualTo(1)
        assertThat(Timber.forest().first() as? DebugTree).isNotNull()
    }
}