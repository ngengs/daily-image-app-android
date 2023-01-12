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
data class PaginationData<T : Parcelable>(
    @Json(name = "pagination")
    val pagination: Pagination,
    @Json(name = "data")
    val data: List<T>
) : Parcelable
