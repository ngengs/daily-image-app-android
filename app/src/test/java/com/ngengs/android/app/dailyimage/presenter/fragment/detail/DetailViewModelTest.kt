package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeChangeFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetFavoriteStatusUseCase
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
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
class DetailViewModelTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeGetFavoriteStatusUseCase = FakeGetFavoriteStatusUseCase()
    private val fakeChangeFavoriteStatusUseCase = FakeChangeFavoriteStatusUseCase()
    private lateinit var viewModel: DetailViewModel


    @Before
    fun setUp() {
        viewModel = DetailViewModel(
            fakeGetFavoriteStatusUseCase,
            fakeChangeFavoriteStatusUseCase,
            dispatcherProvider
        )
    }

    @After
    fun tearDown() {
        fakeGetFavoriteStatusUseCase.reset()
        fakeChangeFavoriteStatusUseCase.reset()
    }

    @Test
    fun test_set__changeFavorite_handleDataCorrectly() = runTest {
        // Given
        val photo = DataForger.forgeParcel<PhotosLocal>(forge)
        val isFavorite = forge.aBool()
        fakeGetFavoriteStatusUseCase.status = isFavorite

        viewModel.data.test {
            skipItems(1) // Skip initialize

            // When Set Data
            viewModel.set(photo)

            // Then Set Data
            viewModel.photo shouldBe photo
            awaitItem().isFavorite shouldBe isFavorite

            // When Change Favorite
            viewModel.changeFavorite()

            // Then Change Favorite
            awaitItem().isFavorite shouldBe !isFavorite
            fakeChangeFavoriteStatusUseCase.status shouldBe !isFavorite
        }
    }
}