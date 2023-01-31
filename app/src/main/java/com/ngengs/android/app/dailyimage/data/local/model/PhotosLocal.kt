package com.ngengs.android.app.dailyimage.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase
import com.ngengs.android.app.dailyimage.data.model.UserSimple
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = DailyImageDatabase.TABLE_PHOTO)
data class PhotosLocal(
    @PrimaryKey
    @Json(name = "id")
    @ColumnInfo(name = DailyImageDatabase.COLUMN_ID)
    val id: String = "",
    @Json(name = "width")
    @ColumnInfo(name = "width")
    val width: Int = 0,
    @Json(name = "height")
    @ColumnInfo(name = "height")
    val height: Int = 0,
    @Json(name = "blur_hash")
    @ColumnInfo(name = "blur_hash")
    val blurHash: String = "",
    @Json(name = "color")
    @ColumnInfo(name = "color")
    val color: String = "#FFFFFF",
    @Json(name = "description")
    @ColumnInfo(name = "description")
    val description: String? = null,
    @Json(name = "image")
    @ColumnInfo(name = "image")
    val image: String = "",
    @Json(name = "user")
    @ColumnInfo(name = "user")
    val user: UserSimple? = null,
) : Parcelable