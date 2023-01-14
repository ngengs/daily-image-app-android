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
data class User(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "username")
    val username: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "portfolio_url")
    val portfolioUrl: String? = null,
    @Json(name = "bio")
    val bio: String? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "total_likes")
    val totalLikes: Int? = null,
    @Json(name = "total_photos")
    val totalPhotos: Int? = null,
    @Json(name = "total_collections")
    val totalCollections: Int? = null,
    @Json(name = "instagram_username")
    val instagramUsername: String? = null,
    @Json(name = "twitter_username")
    val twitterUsername: String? = null,
    @Json(name = "profile_image")
    val profileImage: ProfileImage = ProfileImage(),
    @Json(name = "links")
    val links: UserLinks = UserLinks()
) : Parcelable
