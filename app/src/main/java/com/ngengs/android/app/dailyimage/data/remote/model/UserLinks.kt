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
data class UserLinks(
    @Json(name = "self")
    val self: String = "",
    @Json(name = "html")
    val html: String = "",
    @Json(name = "photos")
    val photos: String = "",
    @Json(name = "likes")
    val likes: String = "",
    @Json(name = "portfolio")
    val portfolio: String = ""
) : Parcelable
