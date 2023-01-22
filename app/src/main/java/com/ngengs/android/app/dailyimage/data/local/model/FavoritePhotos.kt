package com.ngengs.android.app.dailyimage.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
@Entity(
    tableName = DailyImageDatabase.TABLE_FAVORITE,
    indices = [Index(DailyImageDatabase.COLUMN_PHOTO_ID, unique = true)]
)
data class FavoritePhotos(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DailyImageDatabase.COLUMN_ID)
    val id: Long? = null,
    @ColumnInfo(name = DailyImageDatabase.COLUMN_PHOTO_ID)
    val photosId: String,
)