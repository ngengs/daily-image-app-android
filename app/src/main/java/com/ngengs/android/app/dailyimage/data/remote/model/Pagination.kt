package com.ngengs.android.app.dailyimage.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Pagination(
    @Json(name = "first")
    val first: Long = 1L,
    @Json(name = "prev")
    val prev: Long = 1L,
    @Json(name = "next")
    val next: Long = 1L,
    @Json(name = "last")
    val last: Long = 1L,
) : Parcelable
