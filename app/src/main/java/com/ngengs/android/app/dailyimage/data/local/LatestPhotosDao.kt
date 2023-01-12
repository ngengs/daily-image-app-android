package com.ngengs.android.app.dailyimage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_PHOTO_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.TABLE_LATEST
import com.ngengs.android.app.dailyimage.data.local.model.LatestPhotos
import com.ngengs.android.app.dailyimage.data.local.model.LatestPhotosRelation

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Dao
abstract class LatestPhotosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(data: List<LatestPhotos>)

    @Query("DELETE FROM $TABLE_LATEST")
    abstract suspend fun clear()

    @Query(
        "SELECT * FROM $TABLE_LATEST WHERE $COLUMN_PHOTO_ID in (:photoIds) ORDER BY $COLUMN_ID ASC"
    )
    abstract suspend fun get(photoIds: List<String>): List<LatestPhotosRelation>

    @Query("SELECT * FROM $TABLE_LATEST ORDER BY $COLUMN_ID ASC")
    abstract suspend fun get(): List<LatestPhotosRelation>
}