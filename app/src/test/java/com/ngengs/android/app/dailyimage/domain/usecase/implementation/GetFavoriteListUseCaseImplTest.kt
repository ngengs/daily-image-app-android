package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakeFavoriteRepository
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeNull
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
import com.ngengs.android.libs.test.utils.ext.shouldInstanceOf
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoriteListUseCaseImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakeFavoriteRepository()
    private lateinit var useCase: GetFavoriteListUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetFavoriteListUseCaseImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun test_get_list_favorite() = runTest {
        // Given
        val data = (1..10).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }

        // When Get Use Case
        useCase.invoke().test {
            // When Favorite is empty in repository
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Failure::class
            val firstResult = firstItem as Results.Failure
            firstResult.type shouldBe EMPTY
            firstResult.oldData.shouldBeNull()

            // When Favorite Added or exist in repository
            fakeRepository.addFavorite(data)
            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Success::class
            val secondResult = secondItem as Results.Success
            secondResult.data.isComplete.shouldBeTrue()
            secondResult.data.data shouldBe data
        }
    }
}