package com.ngengs.android.app.dailyimage.utils.common.ext

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */

fun String.toCapitalize() = this.replaceFirstChar { it.titlecase() }
fun String.toTitleCase() = this.split(" ").joinToString(" ") { it.toCapitalize() }