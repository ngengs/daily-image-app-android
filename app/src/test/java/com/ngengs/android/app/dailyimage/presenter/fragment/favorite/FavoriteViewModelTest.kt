package com.ngengs.android.app.dailyimage.presenter.fragment.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetFavoriteListUseCase
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
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
class FavoriteViewModelTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeGetFavoriteListUseCase = FakeGetFavoriteListUseCase()
    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setUp() {
        viewModel = FavoriteViewModel(fakeGetFavoriteListUseCase, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeGetFavoriteListUseCase.reset()
    }

    @Test
    fun test_init() = runTest {
        // Given
        val data = DataForger.forgeParcelListStableId<PhotosLocal>(forge, 20)

        // When & Then
        viewModel.data.test {
            val initialItem = awaitItem()
            initialItem.mainData shouldInstanceOf Results.Loading::class

            // When data given from use case
            fakeGetFavoriteListUseCase.emit(data)
            // Then
            val firstItem = awaitItem()
            firstItem.mainData shouldInstanceOf Results.Success::class
            val firstResult = firstItem.mainData as Results.Success
            firstResult.data.data shouldBe data
        }
    }

    @Test
    fun test_changeViewType_getViewType() = runTest {
        // When
        val resultInitial = viewModel.getViewType()
        // Then
        resultInitial shouldBe ViewConstant.VIEW_TYPE_GRID

        // When
        viewModel.changeViewType()
        val resultChanged = viewModel.getViewType()
        // Then
        resultChanged shouldBe ViewConstant.VIEW_TYPE_LIST
    }
}