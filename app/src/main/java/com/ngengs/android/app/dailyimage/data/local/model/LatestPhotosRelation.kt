package com.ngengs.android.app.dailyimage.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
data class LatestPhotosRelation(
    @Relation(
        parentColumn = DailyImageDatabase.COLUMN_PHOTO_ID,
        entityColumn = DailyImageDatabase.COLUMN_ID,
    )
    val photos: PhotosLocal?,
    @Embedded
    val latest: LatestPhotos,
)