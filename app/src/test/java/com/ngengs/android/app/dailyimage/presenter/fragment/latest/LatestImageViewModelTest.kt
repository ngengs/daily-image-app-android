package com.ngengs.android.app.dailyimage.presenter.fragment.latest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetPhotoListUseCase
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.ORDER_BY_LATEST
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.ORDER_BY_POPULAR
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_GRID
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_LIST
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
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
class LatestImageViewModelTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeGetPhotoListUseCase = FakeGetPhotoListUseCase()
    private lateinit var viewModel: LatestImageViewModel

    @Before
    fun setUp() {
        viewModel = LatestImageViewModel(fakeGetPhotoListUseCase, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeGetPhotoListUseCase.reset()
    }

    @Test
    fun test_init_and_reload() = runTest {
        // Given
        val cache = DataForger.forgeParcelListStableId<PhotosLocal>(forge, 5)
        val data = DataForger.forgeParcelListStableId<PhotosLocal>(forge, 20)
        val cacheResult = Results.Success(CompletableCachedData(true, cache, isCache = true))
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))

        // When & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            firstItem.mainData shouldInstanceOf Results.Loading::class
            firstItem.cache.shouldBeEmpty()
            firstItem.page shouldBe 1

            // Mock perform result cache from use-case
            fakeGetPhotoListUseCase.emitResult(cacheResult)
            val secondItem = awaitItem()
            secondItem.cache shouldBe cache
            secondItem.page shouldBe 1
            secondItem.mainData shouldInstanceOf Results.Loading::class

            // Mock perform result data from use-case
            fakeGetPhotoListUseCase.emitResult(mainResult)
            val thirdItem = awaitItem()
            thirdItem.cache shouldBe cache
            thirdItem.page shouldBe 2
            thirdItem.mainData shouldInstanceOf Results.Success::class
            val thirdItemMainData = thirdItem.mainData as Results.Success
            thirdItemMainData.data.isComplete.shouldBeFalse()
            thirdItemMainData.data.data shouldBe data
        }
    }


    @Test
    fun test_fetchNextIfNeeded() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization
        val data = DataForger.forgeParcelListStableId<PhotosLocal>(forge, 20)
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))
        val mainResult2 = Results.Success(CompletableCachedData(true, data, isCache = false))

        // When & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            firstItem.mainData shouldInstanceOf Results.Loading::class
            firstItem.cache.shouldBeEmpty()
            firstItem.page shouldBe 1

            // When fetchNextIfNeeded Then assert do nothing when fetch next but still loading
            viewModel.fetchNextIfNeeded()
            expectNoEvents()

            // Make sure data changed
            viewModel.setInitialData(viewModel.data.value.copy(page = 2L, mainData = mainResult))
            val secondItem = awaitItem()
            secondItem.mainData shouldInstanceOf Results.Success::class
            secondItem.page shouldBe 2

            // Make sure main job stopped and can't change data from flow
            viewModel.stopRunningJob()
            fakeGetPhotoListUseCase.emitResult(mainResult)
            expectNoEvents()

            // When fetchNextIfNeeded Then make sure next data fetched
            viewModel.fetchNextIfNeeded()
            val thirdItem = awaitItem()
            thirdItem.mainData shouldInstanceOf Results.Success::class
            val thirdItemData = thirdItem.mainData as Results.Success
            thirdItemData.data.data shouldBe data
            thirdItemData.data.isComplete.shouldBeFalse()

            // Make sure can't fetch next data if already complete
            fakeGetPhotoListUseCase.emitResult(mainResult2) // Change data to complete
            skipItems(1) // We don't need to check result
            viewModel.fetchNextIfNeeded()
            expectNoEvents()
        }
    }

    @Test
    fun test_changeViewType_getViewType() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization

        // When & Then
        viewModel.data.test {
            awaitItem().viewType shouldBe VIEW_TYPE_GRID // Check initialized data

            // When
            viewModel.changeViewType()

            // Then view type changed
            val firstItem = awaitItem()
            firstItem.viewType shouldBe VIEW_TYPE_LIST
            viewModel.getViewType() shouldBe VIEW_TYPE_LIST

            // When
            viewModel.changeViewType()

            // Then view type changed
            val secondItem = awaitItem()
            secondItem.viewType shouldBe VIEW_TYPE_GRID
            viewModel.getViewType() shouldBe VIEW_TYPE_GRID
        }
    }

    @Test
    fun test_changeOrderBy_isOrderByLatest() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization
        val data = DataForger.forgeParcelListStableId<PhotosLocal>(forge, 2)
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))
        val mainResult2 = Results.Success(CompletableCachedData(true, data, isCache = false))

        // When & Then
        viewModel.data.test {
            awaitItem().orderType shouldBe ORDER_BY_LATEST // Check initialized data

            // When
            viewModel.changeOrderBy()

            // Then order type changed
            val firstItem = awaitItem()
            firstItem.orderType shouldBe ORDER_BY_POPULAR
            viewModel.isOrderByLatest().shouldBeFalse()

            // Then main data fetched from server
            fakeGetPhotoListUseCase.emitResult(mainResult)
            val secondItem = awaitItem()
            (secondItem.mainData as Results.Success).data shouldBe mainResult.data
            secondItem.page shouldBe 2
            viewModel.stopRunningJob()

            // When
            viewModel.changeOrderBy()
            skipItems(2) // Skip change order only, check next when reload

            // Then order type changed
            val thirdItem = awaitItem()
            thirdItem.orderType shouldBe ORDER_BY_LATEST
            viewModel.isOrderByLatest().shouldBeTrue()

            // Then main data fetched from server
            fakeGetPhotoListUseCase.emitResult(mainResult2)
            val fourthItem = awaitItem()
            (fourthItem.mainData as Results.Success).data shouldBe mainResult2.data
            fourthItem.page shouldBe 2
            viewModel.stopRunningJob()
        }
    }
}