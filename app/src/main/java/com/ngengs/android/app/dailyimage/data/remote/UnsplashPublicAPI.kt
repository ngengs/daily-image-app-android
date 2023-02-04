package com.ngengs.android.app.dailyimage.data.remote

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.ngengs.android.app.dailyimage.data.remote.model.AutoComplete
import com.ngengs.android.app.dailyimage.utils.network.NetworkHelpers
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface UnsplashPublicAPI {
    @GET("/nautocomplete/{text}")
    suspend fun autocomplete(
        @Path("text") text: String,
    ): AutoComplete

    companion object {
        private const val BASE_URL = "https://unsplash.com/"

        @VisibleForTesting
        fun instantiate(context: Context?, url: String): UnsplashPublicAPI =
            NetworkHelpers.provideAPI(url, UnsplashPublicAPI::class, context)

        fun instantiate(context: Context?) = instantiate(context, BASE_URL)
    }
}