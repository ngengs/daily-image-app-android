package com.ngengs.android.app.dailyimage.utils.ui.ext

import android.content.res.Resources

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()