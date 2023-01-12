package com.ngengs.android.app.dailyimage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.TABLE_PHOTO
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Dao
abstract class PhotosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(photos: List<PhotosLocal>)

    @Query("SELECT * FROM $TABLE_PHOTO WHERE $COLUMN_ID in (:ids)")
    abstract suspend fun get(ids: List<String>): List<PhotosLocal>

    @Query("DELETE FROM $TABLE_PHOTO WHERE $COLUMN_ID in (:ids)")
    abstract suspend fun delete(ids: List<String>)
}