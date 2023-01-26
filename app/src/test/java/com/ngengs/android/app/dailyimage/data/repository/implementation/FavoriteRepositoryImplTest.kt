package com.ngengs.android.app.dailyimage.data.repository.implementation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.source.FakePhotoLocalDataSource
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakePhotoLocalDataSource = FakePhotoLocalDataSource()
    private lateinit var repository: FavoriteRepositoryImpl

    @Before
    fun setUp() {
        repository = FavoriteRepositoryImpl(fakePhotoLocalDataSource, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakePhotoLocalDataSource.reset()
    }

    @Test
    fun test_getFavorite_setFavorite_removeFavorite_success() = runTest {
        // Given
        val data = (1..10).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }

        // When & Then
        repository.get().test {
            skipItems(1) // Skip initialize list

            repository.setFavorite(data[0])
            val firstItem = awaitItem()
            assertThat(firstItem).hasSize(1)
            assertThat(firstItem).isEqualTo(listOf(data[0]))

            repository.setFavorite(data[1])
            val secondItem = awaitItem()
            assertThat(secondItem).hasSize(2)
            assertThat(secondItem).isEqualTo(listOf(data[1], data[0]))

            repository.removeFavorite(data[0])
            val thirdItem = awaitItem()
            assertThat(thirdItem).hasSize(1)
            assertThat(thirdItem).isEqualTo(listOf(data[1]))

            repository.removeFavorite(data[1])
            val fourthItem = awaitItem()
            assertThat(fourthItem).isEmpty()
        }
    }

    @Test
    fun test_isFavorite() = runTest {
        // Given
        val data = (1..10).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        (1..5).forEach { repository.setFavorite(data[it]) }

        // When
        val resultExist = repository.isFavorite(data[forge.anInt(min = 1, max = 5)])
        // Then
        assertThat(resultExist).isTrue()

        // When
        val resultNotExist = repository.isFavorite(data[forge.anInt(min = 6, max = 10)])
        // Then
        assertThat(resultNotExist).isFalse()
    }
}