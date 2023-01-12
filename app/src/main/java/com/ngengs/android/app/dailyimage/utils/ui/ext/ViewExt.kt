package com.ngengs.android.app.dailyimage.utils.ui.ext

import android.view.View

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.visibleIf(predicate: Boolean) {
    this.visibility = if (predicate) View.VISIBLE else View.GONE
}