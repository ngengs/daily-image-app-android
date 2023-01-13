package com.ngengs.android.app.dailyimage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_PHOTO_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.TABLE_POPULAR
import com.ngengs.android.app.dailyimage.data.local.model.PopularPhotos
import com.ngengs.android.app.dailyimage.data.local.model.PopularPhotosRelation

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Dao
abstract class PopularPhotosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(data: List<PopularPhotos>)

    @Query("DELETE FROM $TABLE_POPULAR")
    abstract suspend fun clear()

    @Transaction
    @Query(
        "SELECT * FROM $TABLE_POPULAR WHERE $COLUMN_PHOTO_ID in (:photoIds) ORDER BY $COLUMN_ID ASC"
    )
    abstract suspend fun get(photoIds: List<String>): List<PopularPhotosRelation>

    @Transaction
    @Query("SELECT * FROM $TABLE_POPULAR ORDER BY $COLUMN_ID ASC")
    abstract suspend fun get(): List<PopularPhotosRelation>
}