package com.ngengs.android.app.dailyimage.utils.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */
class NullToEmptyStringAdapter {
    @ToJson
    fun toJson(@NullToEmptyString value: String?): String? {
        return value
    }

    @FromJson
    @NullToEmptyString
    fun fromJson(@javax.annotation.Nullable data: String?): String {
        return data.orEmpty()
    }

    companion object {
        @Retention(AnnotationRetention.RUNTIME)
        @JsonQualifier
        annotation class NullToEmptyString
    }
}