package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakeFavoriteRepository
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
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
class ChangeFavoriteStatusUseCaseImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakeFavoriteRepository()
    private lateinit var useCase: ChangeFavoriteStatusUseCaseImpl

    @Before
    fun setUp() {
        useCase = ChangeFavoriteStatusUseCaseImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun test_change_setFavorite_unFavorite() = runTest {
        // Given
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge)

        // When Set Favorite
        useCase.invoke(data, currentStatus = false)
        // Then
        fakeRepository.isFavorite(data).shouldBeTrue()

        // When UnFavorite
        useCase.invoke(data, currentStatus = true)
        // Then
        fakeRepository.isFavorite(data).shouldBeFalse()
    }
}