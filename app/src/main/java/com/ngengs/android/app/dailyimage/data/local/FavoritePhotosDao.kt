package com.ngengs.android.app.dailyimage.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.COLUMN_PHOTO_ID
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase.Companion.TABLE_FAVORITE
import com.ngengs.android.app.dailyimage.data.local.model.FavoritePhotos
import com.ngengs.android.app.dailyimage.data.local.model.FavoritePhotosRelation
import kotlinx.coroutines.flow.Flow

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
@Dao
abstract class FavoritePhotosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun save(data: FavoritePhotos)

    @Delete
    abstract suspend fun delete(data: FavoritePhotos)

    @Transaction
    @Query(
        "SELECT * FROM $TABLE_FAVORITE WHERE $COLUMN_PHOTO_ID = :photoIds ORDER BY $COLUMN_ID ASC"
    )
    abstract suspend fun get(photoIds: String): FavoritePhotosRelation?

    @Transaction
    @Query("SELECT * FROM $TABLE_FAVORITE ORDER BY $COLUMN_ID DESC")
    abstract fun get(): Flow<List<FavoritePhotosRelation>>

    @Transaction
    @Query("SELECT * FROM $TABLE_FAVORITE ORDER BY $COLUMN_ID DESC")
    abstract suspend fun getAll(): List<FavoritePhotosRelation>
}