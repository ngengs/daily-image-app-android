package com.ngengs.android.app.dailyimage.utils.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.ngengs.android.app.dailyimage.utils.network.NetworkHelpers
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
@GlideModule
class DailyImageGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(
                NetworkHelpers.provideOkHttp(
                    context,
                    debugTag = TAG,
                    debugLoggingLevel = HttpLoggingInterceptor.Level.BASIC
                )
            )
        )
        super.registerComponents(context, glide, registry)
    }

    companion object {
        private const val TAG = "Glide-OkHttp"
    }
}