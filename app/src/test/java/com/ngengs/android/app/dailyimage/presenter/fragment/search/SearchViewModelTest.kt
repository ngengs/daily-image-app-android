package com.ngengs.android.app.dailyimage.presenter.fragment.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchSuggestionUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchedPhotoUseCase
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
import com.ngengs.android.libs.test.utils.ext.shouldBeNull
import com.ngengs.android.libs.test.utils.ext.shouldInstanceOf
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeGetSearchedPhotoUseCase = FakeGetSearchedPhotoUseCase()
    private val fakeGetSearchSuggestionUseCase = FakeGetSearchSuggestionUseCase()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(
            fakeGetSearchedPhotoUseCase,
            fakeGetSearchSuggestionUseCase,
            dispatcherProvider
        )
    }

    @After
    fun tearDown() {
        fakeGetSearchedPhotoUseCase.reset()
        fakeGetSearchSuggestionUseCase.reset()
    }

    @Test
    fun test_setText_and_reload() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val isComplete = forge.aBool()
        val mainResult = Results.Success(CompletableCachedData(isComplete, data))
        val searchText = forge.anAlphabeticalString(size = 10)

        // WHen & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            firstItem.mainData shouldInstanceOf Results.Loading::class
            firstItem.page shouldBe 1
            firstItem.text.shouldBeNull()

            // When
            viewModel.setText(searchText)

            // Then make sure text changed
            val secondItem = awaitItem()
            secondItem.text shouldBe searchText
            secondItem.page shouldBe 1

            // Then make sure fetch data handled
            fakeGetSearchedPhotoUseCase.emitResult(Results.Failure(Exception()))
            val thirdItem = awaitItem()
            thirdItem.page shouldBe 1
            thirdItem.mainData shouldInstanceOf Results.Failure::class

            fakeGetSearchedPhotoUseCase.emitResult(mainResult)
            val fourthItem = awaitItem()
            fourthItem.page shouldBe 2
            fourthItem.mainData shouldInstanceOf Results.Success::class
            val fourthItemMainData = fourthItem.mainData as Results.Success
            fourthItemMainData.data.isComplete shouldBe isComplete
            fourthItemMainData.data.data shouldBe data
        }
    }

    @Test
    fun test_fetchNextIfNeeded() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val mainResult = Results.Success(CompletableCachedData(false, data))
        val mainResult2 = Results.Success(CompletableCachedData(true, data))
        val searchText = forge.anAlphabeticalString(size = 10)

        // WHen & Then
        viewModel.data.test {
            // Check initialized data
            val firstItem = awaitItem()
            firstItem.mainData shouldInstanceOf Results.Loading::class
            firstItem.page shouldBe 1
            firstItem.text.shouldBeNull()
            // Set text without fetch data
            viewModel.setInitialData(viewModel.data.value.copy(text = searchText))
            awaitItem().text shouldBe searchText

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
            fakeGetSearchedPhotoUseCase.emitResult(mainResult)
            expectNoEvents()

            // When fetchNextIfNeeded Then make sure next data fetched
            viewModel.fetchNextIfNeeded()
            val thirdItem = awaitItem()
            thirdItem.mainData shouldInstanceOf Results.Success::class
            val thirdItemData = thirdItem.mainData as Results.Success
            thirdItemData.data.data shouldBe data
            thirdItemData.data.isComplete.shouldBeFalse()

            // Make sure can't fetch next data if already complete
            fakeGetSearchedPhotoUseCase.emitResult(mainResult2) // Change data to complete
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
            secondItem.searchSuggestion shouldBe searchSuggestion

            // When
            viewModel.resetSearchSuggestion()

            // Then suggestion must cleared
            val thirdItem = awaitItem()
            thirdItem.searchSuggestion.shouldBeEmpty()
        }
    }

    @Test
    fun test_changeViewType_getViewType() = runTest {
        // Given
        viewModel.stopRunningJob() // Stop changing data from initialization

        // When & Then
        viewModel.data.test {
            awaitItem().viewType shouldBe ViewConstant.VIEW_TYPE_GRID // Check initialized data

            // When
            viewModel.changeViewType()

            // Then view type changed
            val firstItem = awaitItem()
            firstItem.viewType shouldBe ViewConstant.VIEW_TYPE_LIST
            viewModel.getViewType() shouldBe ViewConstant.VIEW_TYPE_LIST

            // When
            viewModel.changeViewType()

            // Then view type changed
            val secondItem = awaitItem()
            secondItem.viewType shouldBe ViewConstant.VIEW_TYPE_GRID
            viewModel.getViewType() shouldBe ViewConstant.VIEW_TYPE_GRID
        }
    }
}