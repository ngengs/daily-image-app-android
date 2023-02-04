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
data class ImageLinks(
    @Json(name = "self")
    val self: String = "",
    @Json(name = "html")
    val html: String = "",
    @Json(name = "download")
    val download: String = "",
    @Json(name = "download_location")
    val downloadLocation: String = "",
) : Parcelable
