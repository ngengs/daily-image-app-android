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
data class AutoCompleteText(
    @Json(name = "query")
    val query: String = "",
    @Json(name = "priority")
    val priority: Long = 0,
) : Parcelable
