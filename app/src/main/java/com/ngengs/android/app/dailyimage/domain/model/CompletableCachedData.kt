package com.ngengs.android.app.dailyimage.domain.model

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
data class CompletableCachedData<T : Parcelable>(
    @Json(name = "is_complete")
    val isComplete: Boolean,
    @Json(name = "data")
    val data: List<T>,
    @Json(name = "is_cache")
    val isCache: Boolean = false,
) : Parcelable