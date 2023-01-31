package com.ngengs.android.app.dailyimage.data.model

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
data class UserSimple(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "username")
    val username: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "avatar")
    val avatar: String = "",
) : Parcelable
