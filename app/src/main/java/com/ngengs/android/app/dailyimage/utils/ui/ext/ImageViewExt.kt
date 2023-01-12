package com.ngengs.android.app.dailyimage.utils.ui.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ngengs.android.app.dailyimage.utils.image.GlideApp
import com.ngengs.android.app.dailyimage.utils.image.ImageOptions

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
@SuppressLint("CheckResult")
fun ImageView?.load(
    url: String,
    builder: ImageOptions.() -> Unit = {}
) {
    if (this == null) return
    val validContext = getValidContext(this) ?: return
    val base = ImageOptions()
    base.builder()
    if (url.isBlank()) {
        GlideApp.with(validContext).load(base.imageOnFail).apply(base.toRequestOptions()).into(this)
    } else {
        val requestBuilder = GlideApp.with(validContext).load(url)

        if (base.onImageLoaded != null || base.onLoadFailed != null) {
            requestBuilder.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    base.onLoadFailed?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    base.onImageLoaded?.invoke(resource)
                    return false
                }
            })
        }

        requestBuilder.apply(base.toRequestOptions())
        if (base.thumbnail != null) requestBuilder.thumbnail(base.thumbnail)
        if (base.crossFade) requestBuilder.transition(DrawableTransitionOptions.withCrossFade())

        requestBuilder.into(this)
    }
}

fun ImageView?.clearLoad() {
    if (this == null) return
    val validContext = getValidContext(this) ?: return

    Glide.with(validContext).clear(this)
}

private fun getValidContext(imageView: ImageView?): Context? {
    if (imageView == null) return null

    val context = when (val rawContext = imageView.context) {
        is Activity -> rawContext
        is ContextWrapper -> rawContext.baseContext
        else -> rawContext
    }

    if (context is Activity) {
        if (context.isFinishing || context.isDestroyed) return null
    }
    return context
}