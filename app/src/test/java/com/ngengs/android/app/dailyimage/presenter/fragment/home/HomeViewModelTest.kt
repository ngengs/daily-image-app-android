package com.ngengs.android.app.dailyimage.presenter.fragment.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetPhotoListUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchSuggestionUseCase
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
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
}