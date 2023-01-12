package com.ngengs.android.app.dailyimage.utils.network

import android.content.Context
import com.ngengs.android.app.dailyimage.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
object NetworkHelpers {
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    private const val CONNECT_TIMEOUT = 15
    private const val WRITE_TIMEOUT = 60
    private const val TIMEOUT = 60

    fun provideOkHttp(
        context: Context?,
        customInterceptor: ((Interceptor.Chain) -> Request)? = null,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }
        if (BuildConfig.DEBUG) loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        else loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)

        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        if (customInterceptor != null) {
            builder.addInterceptor { it.proceed(customInterceptor.invoke(it)) }
        }

        if (context != null) {
            builder.cache(Cache(context.cacheDir, CACHE_SIZE))
            builder.addNetworkInterceptor(CacheInterceptor())
        }

        return builder.build()
    }

    private fun provideRetrofit(okHttpClient: OkHttpClient, apiBaseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(MoshiConfig.moshi))
            .client(okHttpClient)
            .build()

    fun <T : Any> provideAPI(
        apiBaseUrl: String,
        apiClass: KClass<T>,
        context: Context?,
        customInterceptor: ((Interceptor.Chain) -> Request)? = null,
    ): T = provideRetrofit(provideOkHttp(context, customInterceptor), apiBaseUrl)
        .create(apiClass.java)
}