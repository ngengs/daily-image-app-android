package com.ngengs.android.app.dailyimage.data.source.implementation

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
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
@OptIn(ExperimentalCoroutinesApi::class)
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
    fun getFavorites_saveFavorite_deleteFavorite_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When & Then
        dataSource.getFavorites().test {
            dataSource.saveFavorite(data[0])
            skipItems(1) // Skip initialize table

            val firstItem = awaitItem()
            assertThat(firstItem).hasSize(1)
            assertThat(firstItem).isEqualTo(listOf(data[0]))

            dataSource.saveFavorite(data[1])
            val secondItem = awaitItem()
            assertThat(secondItem).hasSize(2)
            assertThat(secondItem).isEqualTo(listOf(data[1], data[0]))

            dataSource.deleteFavorite(data[0])
            val thirdItem = awaitItem()
            assertThat(thirdItem).hasSize(1)
            assertThat(thirdItem).isEqualTo(listOf(data[1]))

            dataSource.deleteFavorite(data[1])
            val fourthItem = awaitItem()
            assertThat(fourthItem).isEmpty()
        }
    }

    @Test
    fun getFavorite_success() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.saveFavorite(data[0])
        val result = dataSource.getFavorite(data[0].id)

        assertThat(result).isEqualTo(data[0])
    }

    @Test
    fun getFavorite_notExist() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }

        // When
        dataSource.saveFavorite(data[0])
        val result = dataSource.getFavorite(data[1].id)

        assertThat(result).isNull()
    }

    @Test
    fun clearPopular_keep_latestDataAndFavoriteData() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }
        val dataPopular = data.subList(0, 5)
        val dataLatest = data.subList(5, 7)
        val dataFavorite = data.subList(7, 10)

        // When
        dataSource.savePopular(dataPopular)
        dataSource.saveLatest(dataLatest)
        dataFavorite.forEach { dataSource.saveFavorite(it) }
        dataSource.clearPopular()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val latestData = database.latestDao().get()
        val favoriteData = database.favoriteDao().getAll()
        val popularData = database.popularDao().get()
        assertThat(photosData).hasSize(dataLatest.size + favoriteData.size)
        assertThat(latestData).hasSize(dataLatest.size)
        assertThat(favoriteData).hasSize(dataFavorite.size)
        assertThat(popularData).isEmpty()
    }

    @Test
    fun clearLatest_keep_popularData() = runTest {
        // Given
        val data = (1..10).map {
            DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        }
        val dataLatest = data.subList(0, 5)
        val dataPopular = data.subList(5, 7)
        val dataFavorite = data.subList(7, 10)

        // When
        dataSource.savePopular(dataPopular)
        dataSource.saveLatest(dataLatest)
        dataFavorite.forEach { dataSource.saveFavorite(it) }
        dataSource.clearLatest()

        // Then
        val photosData = database.photosDao().get(data.map { it.id })
        val latestData = database.latestDao().get()
        val favoriteData = database.favoriteDao().getAll()
        val popularData = database.popularDao().get()
        assertThat(photosData).hasSize(dataPopular.size + dataFavorite.size)
        assertThat(popularData).hasSize(dataPopular.size)
        assertThat(favoriteData).hasSize(dataFavorite.size)
        assertThat(latestData).isEmpty()
    }
}