package com.ngengs.android.app.dailyimage.presenter.fragment.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetPhotoListUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchSuggestionUseCase
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.ORDER_BY_LATEST
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.ORDER_BY_POPULAR
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_GRID
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_LIST
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeGetPhotoListUseCase = FakeGetPhotoListUseCase()
    private val fakeGetSearchSuggestionUseCase = FakeGetSearchSuggestionUseCase()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(
            fakeGetPhotoListUseCase,
            fakeGetSearchSuggestionUseCase,
            dispatcherProvider
        )
    }

    @After
    fun tearDown() {
        fakeGetPhotoListUseCase.reset()
        fakeGetSearchSuggestionUseCase.reset()
    }

    @Test
    fun test_init_and_reload() = runTest {
        // Given
        val cache = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val cacheResult = Results.Success(CompletableCachedData(true, cache, isCache = true))
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))

        // When & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            assertThat(firstItem.mainData).isInstanceOf(Results.Loading::class.java)
            assertThat(firstItem.cache).isEmpty()
            assertThat(firstItem.page).isEqualTo(1)

            // Mock perform result cache from use-case
            fakeGetPhotoListUseCase.emitResult(cacheResult)
            val secondItem = awaitItem()
            assertThat(secondItem.cache).isEqualTo(cache)
            assertThat(secondItem.page).isEqualTo(1)
            assertThat(secondItem.mainData).isInstanceOf(Results.Loading::class.java)

            // Mock perform result data from use-case
            fakeGetPhotoListUseCase.emitResult(mainResult)
            val thirdItem = awaitItem()
            assertThat(thirdItem.cache).isEqualTo(cache)
            assertThat(thirdItem.page).isEqualTo(2)
            assertThat(thirdItem.mainData).isInstanceOf(Results.Success::class.java)
            val thirdItemMainData = thirdItem.mainData as Results.Success
            assertThat(thirdItemMainData.data.isComplete).isFalse()
            assertThat(thirdItemMainData.data.data).isEqualTo(data)
        }
    }

    @Test
    fun test_fetchNextIfNeeded() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))
        val mainResult2 = Results.Success(CompletableCachedData(true, data, isCache = false))

        // When & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            assertThat(firstItem.mainData).isInstanceOf(Results.Loading::class.java)
            assertThat(firstItem.cache).isEmpty()
            assertThat(firstItem.page).isEqualTo(1)

            // When fetchNextIfNeeded Then assert do nothing when fetch next but still loading
            viewModel.fetchNextIfNeeded()
            expectNoEvents()

            // Make sure data changed
            viewModel.setInitialData(viewModel.data.value.copy(page = 2L, mainData = mainResult))
            val secondItem = awaitItem()
            assertThat(secondItem.mainData).isInstanceOf(Results.Success::class.java)
            assertThat(secondItem.page).isEqualTo(2)

            // Make sure main job stopped and can't change data from flow
            viewModel.stopRunningJob()
            fakeGetPhotoListUseCase.emitResult(mainResult)
            expectNoEvents()

            // When fetchNextIfNeeded Then make sure next data fetched
            viewModel.fetchNextIfNeeded()
            val thirdItem = awaitItem()
            assertThat(thirdItem.mainData).isInstanceOf(Results.Success::class.java)
            val thirdItemData = thirdItem.mainData as Results.Success
            assertThat(thirdItemData.data.data).isEqualTo(data)
            assertThat(thirdItemData.data.isComplete).isFalse()

            // Make sure can't fetch next data if already complete
            fakeGetPhotoListUseCase.emitResult(mainResult2) // Change data to complete
            skipItems(1) // We don't need to check result
            viewModel.fetchNextIfNeeded()
            expectNoEvents()
        }
    }

    @Test
    fun test_onTypedSearch_resetSearchSuggestion() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization
        val searchSuggestion = (5..15).map { forge.anAlphabeticalString(size = it) }
        fakeGetSearchSuggestionUseCase.suggestionList = searchSuggestion

        val text = forge.anAlphaNumericalString(size = 10)

        // When & Then
        viewModel.data.test {
            skipItems(1) // Skip initialized data

            // When typed search
            viewModel.onTypedSearch(text)

            // Then suggestion must provided
            val secondItem = awaitItem()
            assertThat(secondItem.searchSuggestion).isEqualTo(searchSuggestion)

            // When
            viewModel.resetSearchSuggestion()

            // Then suggestion must cleared
            val thirdItem = awaitItem()
            assertThat(thirdItem.searchSuggestion).isEmpty()
        }
    }

    @Test
    fun test_changeViewType_getViewType() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization

        // When & Then
        viewModel.data.test {
            assertThat(awaitItem().viewType).isEqualTo(VIEW_TYPE_GRID) // Check initialized data

            // When
            viewModel.changeViewType()

            // Then view type changed
            val firstItem = awaitItem()
            assertThat(firstItem.viewType).isEqualTo(VIEW_TYPE_LIST)
            assertThat(viewModel.getViewType()).isEqualTo(VIEW_TYPE_LIST)

            // When
            viewModel.changeViewType()

            // Then view type changed
            val secondItem = awaitItem()
            assertThat(secondItem.viewType).isEqualTo(VIEW_TYPE_GRID)
            assertThat(viewModel.getViewType()).isEqualTo(VIEW_TYPE_GRID)
        }
    }

    @Test
    fun test_changeOrderBy_isOrderByLatest() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization
        val data = (1..2).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val mainResult = Results.Success(CompletableCachedData(false, data, isCache = false))
        val mainResult2 = Results.Success(CompletableCachedData(true, data, isCache = false))

        // When & Then
        viewModel.data.test {
            assertThat(awaitItem().orderType).isEqualTo(ORDER_BY_LATEST) // Check initialized data

            // When
            viewModel.changeOrderBy()

            // Then order type changed
            val firstItem = awaitItem()
            assertThat(firstItem.orderType).isEqualTo(ORDER_BY_POPULAR)
            assertThat(viewModel.isOrderByLatest()).isFalse()

            // Then main data fetched from server
            fakeGetPhotoListUseCase.emitResult(mainResult)
            val secondItem = awaitItem()
            assertThat((secondItem.mainData as Results.Success).data).isEqualTo(mainResult.data)
            assertThat(secondItem.page).isEqualTo(2)
            viewModel.stopRunningJob()

            // When
            viewModel.changeOrderBy()
            skipItems(2) // Skip change order only, check next when reload

            // Then order type changed
            val thirdItem = awaitItem()
            assertThat(thirdItem.orderType).isEqualTo(ORDER_BY_LATEST)
            assertThat(viewModel.isOrderByLatest()).isTrue()

            // Then main data fetched from server
            fakeGetPhotoListUseCase.emitResult(mainResult2)
            val fourthItem = awaitItem()
            assertThat((fourthItem.mainData as Results.Success).data).isEqualTo(mainResult2.data)
            assertThat(fourthItem.page).isEqualTo(2)
            viewModel.stopRunningJob()
        }
    }
}