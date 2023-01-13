package com.ngengs.android.app.dailyimage.data.remote

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.ngengs.android.app.dailyimage.BuildConfig
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.data.remote.model.SearchResult
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.OrderBy
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.PER_PAGE
import com.ngengs.android.app.dailyimage.utils.network.NetworkHelpers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface UnsplashAPI {
    @GET("/photos?per_page=$PER_PAGE")
    suspend fun photos(
        @Query("page") page: Long,
        @OrderBy @Query("order_by") orderBy: String,
    ): Response<List<Photos>>

    @GET("/search/photos?per_page=$PER_PAGE")
    suspend fun search(
        @Query("page") page: Long,
        @Query("query") query: String,
    ): SearchResult

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"

        @VisibleForTesting
        fun instantiate(context: Context?, url: String): UnsplashAPI =
            NetworkHelpers.provideAPI(url, UnsplashAPI::class, context) {
                val original = it.request()
                val originalHeaders = original.headers
                val newHeaders = originalHeaders.newBuilder()
                if (originalHeaders["Authorization"] == null) {
                    val apiToken = BuildConfig.UNSPLASH_API_KEY
                    newHeaders.add("Authorization", "Client-ID $apiToken")
                }

                original.newBuilder()
                    .headers(newHeaders.build())
                    .build()
            }

        fun instantiate(context: Context?) = instantiate(context, BASE_URL)
    }
}