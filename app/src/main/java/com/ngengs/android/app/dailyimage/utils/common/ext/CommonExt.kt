package com.ngengs.android.app.dailyimage.utils.common.ext

import com.ngengs.android.app.dailyimage.BuildConfig
import timber.log.Timber

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */

fun <T> debugTry(block: () -> T?): T? = try {
    block()
} catch (e: Exception) {
    if (BuildConfig.DEBUG) Timber.e(e)
    null
}

suspend fun <T> debugTrySuspend(block: suspend () -> T?): T? = try {
    block()
} catch (e: Exception) {
    if (BuildConfig.DEBUG) Timber.e(e)
    null
}