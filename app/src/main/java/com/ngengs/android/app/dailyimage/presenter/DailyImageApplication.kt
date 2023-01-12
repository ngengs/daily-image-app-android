package com.ngengs.android.app.dailyimage.presenter

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.ngengs.android.app.dailyimage.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@HiltAndroidApp
class DailyImageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        Timber.d("onCreate")
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}