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
data class ImageUrls(
    @Json(name = "raw")
    val raw: String = "",
    @Json(name = "full")
    val full: String = "",
    @Json(name = "regular")
    val regular: String = "",
    @Json(name = "small")
    val small: String = "",
    @Json(name = "thumb")
    val thumb: String = ""
) : Parcelable
