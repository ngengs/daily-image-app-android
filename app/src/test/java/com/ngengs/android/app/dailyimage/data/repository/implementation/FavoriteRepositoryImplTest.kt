package com.ngengs.android.app.dailyimage.data.repository.implementation

import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.source.FakePhotoLocalDataSource
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
import com.ngengs.android.libs.test.utils.ext.shouldHasSize
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
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 10)

        // When & Then
        repository.get().test {
            skipItems(1) // Skip initialize list

            repository.setFavorite(data[0])
            val firstItem = awaitItem()
            firstItem shouldHasSize 1
            firstItem shouldBe listOf(data[0])

            repository.setFavorite(data[1])
            val secondItem = awaitItem()
            secondItem shouldHasSize 2
            secondItem shouldBe listOf(data[1], data[0])

            repository.removeFavorite(data[0])
            val thirdItem = awaitItem()
            thirdItem shouldHasSize 1
            thirdItem shouldBe listOf(data[1])

            repository.removeFavorite(data[1])
            val fourthItem = awaitItem()
            fourthItem.shouldBeEmpty()
        }
    }

    @Test
    fun test_isFavorite() = runTest {
        // Given
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 10)
        (1..5).forEach { repository.setFavorite(data[it]) }

        // When
        val resultExist = repository.isFavorite(data[forge.anInt(min = 1, max = 5)])
        // Then
        resultExist.shouldBeTrue()

        // When
        val resultNotExist = repository.isFavorite(data[forge.anInt(min = 6, max = 10)])
        // Then
        resultNotExist.shouldBeFalse()
    }
}