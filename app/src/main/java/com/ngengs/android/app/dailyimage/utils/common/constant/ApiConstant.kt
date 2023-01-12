package com.ngengs.android.app.dailyimage.utils.common.constant

import androidx.annotation.StringDef

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
object ApiConstant {
    const val PER_PAGE = 20

    const val ORDER_BY_LATEST = "latest"
    const val ORDER_BY_POPULAR = "popular"

    const val HEADER_LINK = "Link"

    @StringDef(ORDER_BY_LATEST, ORDER_BY_POPULAR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OrderBy
}