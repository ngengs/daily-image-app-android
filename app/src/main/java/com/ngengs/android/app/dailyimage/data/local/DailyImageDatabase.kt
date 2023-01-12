package com.ngengs.android.app.dailyimage.data.local

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ngengs.android.app.dailyimage.data.local.model.LatestPhotos
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.local.model.PopularPhotos

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Database(
    entities = [PhotosLocal::class, PopularPhotos::class, LatestPhotos::class],
    version = DailyImageDatabase.VERSION,
    exportSchema = false
)
@TypeConverters(DailyImageDatabaseTypeConverter::class)
abstract class DailyImageDatabase : RoomDatabase() {
    abstract fun photosDao(): PhotosDao
    abstract fun popularDao(): PopularPhotosDao
    abstract fun latestDao(): LatestPhotosDao

    companion object {
        @VisibleForTesting
        const val NAME = "db_daily_image"
        const val VERSION = 1

        const val TABLE_PHOTO = "t_photo"
        const val TABLE_LATEST = "t_latest"
        const val TABLE_POPULAR = "t_popular"

        const val COLUMN_ID = "_id"
        const val COLUMN_PHOTO_ID = "p_id"

        fun initialize(context: Context) =
            Room.databaseBuilder(context, DailyImageDatabase::class.java, NAME)
                .build()
    }
}