package com.ngengs.android.app.dailyimage.utils.common.constant

import androidx.annotation.IntDef

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
object ViewConstant {
    const val VIEW_TYPE_LIST = 2000
    const val VIEW_TYPE_GRID = 2001

    @IntDef(VIEW_TYPE_LIST, VIEW_TYPE_GRID)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PhotoListViewType
}