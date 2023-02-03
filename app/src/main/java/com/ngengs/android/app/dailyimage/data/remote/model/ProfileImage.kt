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
data class ProfileImage(
    @Json(name = "small")
    val small: String = "",
    @Json(name = "medium")
    val medium: String = "",
    @Json(name = "large")
    val large: String = "",
) : Parcelable
