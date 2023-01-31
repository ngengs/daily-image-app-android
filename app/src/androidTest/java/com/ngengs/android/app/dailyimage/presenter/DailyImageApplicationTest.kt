package com.ngengs.android.app.dailyimage.presenter

import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldNotNull
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
        Timber.treeCount shouldBe 1
        (Timber.forest().first() as? DebugTree).shouldNotNull()
    }
}