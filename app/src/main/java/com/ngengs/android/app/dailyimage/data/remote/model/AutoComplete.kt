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
data class AutoComplete(
    @Json(name = "fuzzy")
    val fuzzy: List<AutoCompleteText> = emptyList(),
    @Json(name = "autocomplete")
    val autocomplete: List<AutoCompleteText> = emptyList(),
    @Json(name = "did_you_mean")
    val didYouMean: List<AutoCompleteText> = emptyList(),
) : Parcelable
