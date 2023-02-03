package com.ngengs.android.app.dailyimage.data.remote.model

import android.os.Parcelable
import com.ngengs.android.app.dailyimage.utils.network.adapter.NullToEmptyStringAdapter.Companion.NullToEmptyString
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Photos(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "updated_at")
    val updatedAt: String = "",
    @Json(name = "width")
    val width: Int = 0,
    @Json(name = "height")
    val height: Int = 0,
    @Json(name = "color")
    val color: String = "",
    @NullToEmptyString
    @Json(name = "blur_hash")
    val blurHash: String = "",
    @Json(name = "likes")
    val likes: Int = 0,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "user")
    val user: User = User(),
    @Json(name = "urls")
    val urls: ImageUrls = ImageUrls(),
    @Json(name = "links")
    val links: ImageLinks = ImageLinks(),
) : Parcelable
