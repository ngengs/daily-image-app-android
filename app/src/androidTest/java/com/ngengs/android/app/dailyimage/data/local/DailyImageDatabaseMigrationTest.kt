package com.ngengs.android.app.dailyimage.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.UserSimple
import com.ngengs.android.app.dailyimage.utils.network.MoshiConfig
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldHasSize
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DailyImageDatabaseMigrationTest {
    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DailyImageDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun test_migration_1to2() = runTest {
        // Given
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 2)
        val userSimpleAdapter = MoshiConfig.moshi.adapter(UserSimple::class.java)
        // When version 1
        helper.createDatabase(TEST_DB_NAME, 1).apply {
            insert(
                DailyImageDatabase.TABLE_PHOTO,
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put(DailyImageDatabase.COLUMN_ID, data.first().id)
                    put("width", data.first().width)
                    put("height", data.first().height)
                    put("blur_hash", data.first().blurHash)
                    put("color", data.first().color)
                    put("description", data.first().description)
                    put("image", data.first().image)
                    put("user", userSimpleAdapter.toJson(data.first().user).orEmpty())
                },
            )
            insert(
                DailyImageDatabase.TABLE_PHOTO,
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put(DailyImageDatabase.COLUMN_ID, data.last().id)
                    put("width", data.last().width)
                    put("height", data.last().height)
                    put("blur_hash", data.last().blurHash)
                    put("color", data.last().color)
                    put("description", data.last().description)
                    put("image", data.last().image)
                    put("user", userSimpleAdapter.toJson(data.last().user).orEmpty())
                },
            )

            close()
        }

        // When version 2
        helper.runMigrationsAndValidate(TEST_DB_NAME, 2, true).apply {
            val photosV2 = mutableListOf<PhotosLocal>()
            val cursor = query("SELECT * FROM ${DailyImageDatabase.TABLE_PHOTO}")

            with(cursor) {
                while (moveToNext()) {
                    val photo = PhotosLocal(
                        id = getString(getColumnIndex(DailyImageDatabase.COLUMN_ID)),
                        width = getInt(getColumnIndex("width")),
                        height = getInt(getColumnIndex("height")),
                        blurHash = getString(getColumnIndex("blur_hash")),
                        color = getString(getColumnIndex("color")),
                        description = getStringOrNull(getColumnIndex("description")),
                        image = getString(getColumnIndex("image")),
                        user = getStringOrNull(getColumnIndex("user"))?.let {
                            userSimpleAdapter.fromJson(it)
                        },
                    )
                    photosV2.add(photo)
                }
            }
            cursor.close()

            photosV2 shouldHasSize 2
            photosV2 shouldBe data
        }
    }

    companion object {
        private const val TEST_DB_NAME = "test_migration_db"
    }
}