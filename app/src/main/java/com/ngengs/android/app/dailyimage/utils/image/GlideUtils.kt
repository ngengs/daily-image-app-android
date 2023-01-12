package com.ngengs.android.app.dailyimage.utils.image

import android.content.Context

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
object GlideUtils {
    fun thumbnailBuilder(context: Context, url: String?) =
        GlideApp.with(context).load(url).sizeMultiplier(0.05F)
}