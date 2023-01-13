package com.ngengs.android.app.dailyimage.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class SearchResult(
    @Json(name = "total")
    val total: Long = 0L,
    @Json(name = "total_pages")
    val totalPages: Long = 0L,
    @Json(name = "results")
    val results: List<Photos> = emptyList()
) : Parcelable
