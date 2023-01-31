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
class GetFavoriteStatusUseCaseImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakeFavoriteRepository()
    private lateinit var useCase: GetFavoriteStatusUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetFavoriteStatusUseCaseImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun test_getStatus() = runTest {
        // Given
        val data = (1..10).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val dataFavorite = data.first()
        val dataNotFavorite = data.last()
        fakeRepository.setFavorite(dataFavorite)

        // When Favorite
        val resultFavorite = useCase.invoke(dataFavorite)
        // Then
        resultFavorite.shouldBeTrue()

        // When Not Favorite
        val resultNotFavorite = useCase.invoke(dataNotFavorite)
        // Then
        resultNotFavorite.shouldBeFalse()
    }
}