package com.ngengs.android.app.dailyimage.data.source.implementation

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [32])
class PhotoLocalDataSourceImplTest {
    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private lateinit var dataSource: PhotoLocalDataSourceImpl
    private lateinit var database: DailyImageDatabase

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(getApplicationContext(), DailyImageDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dataSource = PhotoLocalDataSourceImpl(database, dispatcherProvider)
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun savePopular_getPopular_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.savePopular(data)
        val result = dataSource.getPopular()

        // Then
        assertThat(result).hasSize(data.size)
        assertThat(result).isEqualTo(data)
    }

    @Test
    fun clearPopular_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.savePopular(data)
        dataSource.clearPopular()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val popularData = database.popularDao().get(data.map { it.id })
        assertThat(photosData).isEmpty()
        assertThat(popularData).isEmpty()
    }

    @Test
    fun saveLatest_getLatest_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.saveLatest(data)
        val result = dataSource.getLatest()

        // Then
        assertThat(result).hasSize(data.size)
        assertThat(result).isEqualTo(data)
    }

    @Test
    fun clearLatest_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.saveLatest(data)
        dataSource.clearLatest()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val latestData = database.latestDao().get(data.map { it.id })
        assertThat(photosData).isEmpty()
        assertThat(latestData).isEmpty()
    }

    @Test
    fun clearPopular_keep_latestData() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }
        val dataPopular = data.subList(0, 5)
        val dataLatest = data.subList(5, 10)

        // When
        dataSource.savePopular(dataPopular)
        dataSource.saveLatest(dataLatest)
        dataSource.clearPopular()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val latestData = database.latestDao().get()
        val popularData = database.popularDao().get()
        assertThat(photosData).hasSize(5)
        assertThat(latestData).hasSize(5)
        assertThat(popularData).isEmpty()
    }

    @Test
    fun clearLatest_keep_popularData() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }
        val dataPopular = data.subList(0, 5)
        val dataLatest = data.subList(5, 10)

        // When
        dataSource.savePopular(dataPopular)
        dataSource.saveLatest(dataLatest)
        dataSource.clearLatest()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val latestData = database.latestDao().get()
        val popularData = database.popularDao().get()
        assertThat(photosData).hasSize(5)
        assertThat(popularData).hasSize(5)
        assertThat(latestData).isEmpty()
    }
}