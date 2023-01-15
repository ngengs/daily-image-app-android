package com.ngengs.android.app.dailyimage.utils.network

import com.ngengs.android.app.dailyimage.BuildConfig
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        val shouldUseCache = request.header(CACHE_CONTROL_HEADER) != CACHE_CONTROL_NO_CACHE
        if (!shouldUseCache) return originalResponse

        val cacheControl = CacheControl.Builder()
            .maxAge(
                if (BuildConfig.DEBUG) CACHE_TIME_MINUTES_DEBUG else CACHE_TIME_MINUTES,
                TimeUnit.MINUTES
            )
            .build()

        return originalResponse.newBuilder()
            .header(CACHE_CONTROL_HEADER, cacheControl.toString())
            .build()
    }

    companion object {
        const val CACHE_CONTROL_HEADER = "Cache-Control"
        const val CACHE_CONTROL_NO_CACHE = "no-cache"
        const val CACHE_TIME_MINUTES = 30
        const val CACHE_TIME_MINUTES_DEBUG = 240
    }
}