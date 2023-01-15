package com.ngengs.android.app.dailyimage.presenter.fragment.search

import android.view.View
import android.widget.ImageButton
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.di.FakeUseCaseModule
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.CLIENT
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.NETWORK
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.SERVER
import com.ngengs.android.app.dailyimage.helpers.PhotoDataCreator
import com.ngengs.android.app.dailyimage.helpers.espresso.RecyclerViewChildAction.recyclerChildAction
import com.ngengs.android.app.dailyimage.helpers.espresso.RecyclerViewChildMatcher.atPosition
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewMatcher.isNotDisplayed
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewMatcher.withItemHint
import com.ngengs.android.app.dailyimage.helpers.launchCoroutine
import com.ngengs.android.app.dailyimage.helpers.onFragment
import com.ngengs.android.app.dailyimage.launchFragmentInHiltContainer
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragmentTest
import com.ngengs.android.app.dailyimage.presenter.fragment.search.SearchViewModel.ViewData
import com.ngengs.android.app.dailyimage.utils.common.ext.toTitleCase
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Test

@HiltAndroidTest
class SearchFragmentTest : BaseFragmentTest() {
    @Test
    fun test_correctlyRenderData() {
        val mockData1 = PhotoDataCreator.createList(forge, 20)
        val mockData2 = PhotoDataCreator.createList(forge, 10, startAt = 21)
        val resultSuccess1 = Results.Success(CompletableCachedData(false, mockData1))
        val resultSuccess2 =
            Results.Success(CompletableCachedData(true, mockData1 + mockData2))
        val resultLoadingNextPage =
            Results.Loading(oldData = CompletableCachedData(false, mockData1))
        val resultErrorNextPage =
            Results.Failure(
                oldData = CompletableCachedData(true, mockData1 + mockData2),
                throwable = Exception("Failure"),
                type = NETWORK
            )

        val mockSuggestion1 = (1..10).map { forge.anAlphaNumericalString(size = it + 5) }
        val mockSuggestion2 = (11..20).map { forge.anAlphaNumericalString(size = it + 5) }
        FakeUseCaseModule.useCase.getSearchSuggestionUseCase.suggestionList = mockSuggestion1
        val navController = TestNavHostController(testContext)
        navController.setGraph(R.navigation.nav_graph)
        val searchText = forge.anAlphaNumericalString(size = 5)
        val args = SearchFragmentArgs(searchText = searchText)

        val activityScenario = launchFragmentInHiltContainer<SearchFragment>(
            fragmentArgs = args.toBundle(),
            navHostController = navController,
        )
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(Results.Loading())
        }

        // Test Display Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultSuccess1)
        }
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        val headerToolsTitle =
            testContext.getString(R.string.search_images, searchText.toTitleCase())
        onView(withId(R.id.rv))
            .check(matches(isDisplayed()))
            .check(matches(atPosition(0, hasDescendant(withText(headerToolsTitle)))))
            .check(
                matches(
                    atPosition(1, hasDescendant(withText(mockData1.first().user!!.name)))
                )
            )
            .perform(
                scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(mockData1.last().user!!.name))
                )
            )
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.detailFragment)
        navController.popBackStack()

        // Test Pagination Loading
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultLoadingNextPage)
        }
        onView(withId(R.id.rv))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

        // Test Display Next Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultSuccess2)
        }
        onView(withId(R.id.rv))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(mockData1.first().user!!.name))
                )
            )
            .perform(
                scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(mockData2.last().user!!.name))
                )
            )
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(
                recyclerChildAction<View>(R.id.view_type_button) {
                    performClick()
                }
            )

        // Test Error in Next Page
        activityScenario.onFragment<SearchFragment> {
            it.setInitialData(ViewData(page = 2, mainData = resultErrorNextPage))
        }
        onView(withId(R.id.rv)).check(matches(isDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))

        // Test Search 1
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()))
            .perform(click())
        onView(
            allOf(
                withItemHint(R.string.search_image_hint),
                isDescendantOfA(withId(R.id.search_view))
            )
        ).check(matches(isDisplayed()))
            .perform(clearText())
            .perform(typeText("Apple"))
        onView(withId(R.id.rv_suggestion)).check(matches(isDisplayed()))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(mockSuggestion1.last())))
            )
        Thread.sleep(400L) // Delay before navigation for waiting search closed
        onView(withText(mockSuggestion1.last())).perform(click())
        onView(
            allOf(
                withText(mockSuggestion1.last().toTitleCase()),
                isDescendantOfA(withId(R.id.search_bar))
            )
        ).check(matches(isDisplayed()))
        onView(
            withText(
                testContext.getString(R.string.search_images, mockSuggestion1.last().toTitleCase())
            )
        ).check(matches(isDisplayed()))

        // Test Search 2
        FakeUseCaseModule.useCase.getSearchSuggestionUseCase.suggestionList = mockSuggestion2
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()))
            .perform(click())
        val searchViewEditor2 = onView(
            allOf(
                withItemHint(R.string.search_image_hint),
                isDescendantOfA(withId(R.id.search_view))
            )
        )
        searchViewEditor2.check(matches(isDisplayed()))
            .perform(clearText())
            .perform(typeText("Orange"))
        onView(withId(R.id.rv_suggestion)).check(matches(isDisplayed()))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(mockSuggestion2.last())))
            )
        searchViewEditor2.perform(pressImeActionButton())
        Thread.sleep(400L) // Delay before navigation for waiting search closed
        onView(
            allOf(
                withText("Orange".toTitleCase()),
                isDescendantOfA(withId(R.id.search_bar))
            )
        ).check(matches(isDisplayed()))
        onView(
            withText(
                testContext.getString(R.string.search_images, "Orange".toTitleCase())
            )
        ).check(matches(isDisplayed()))

        Thread.sleep(1000L)

        activityScenario.recreate()
    }

    @Test
    fun test_correctlyRenderError() {
        val resultErrorServer =
            Results.Failure(oldData = null, throwable = Exception("Failure"), type = SERVER)
        val resultErrorNetwork =
            Results.Failure(oldData = null, throwable = Exception("Failure"), type = NETWORK)
        val resultErrorClient =
            Results.Failure(oldData = null, throwable = Exception("Failure"), type = CLIENT)
        val resultErrorEmpty =
            Results.Failure(oldData = null, throwable = Exception("Failure"), type = EMPTY)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        val searchText = forge.anAlphaNumericalString(size = 5)
        val args = SearchFragmentArgs(searchText = searchText)

        val activityScenario = launchFragmentInHiltContainer<SearchFragment>(
            fragmentArgs = args.toBundle(),
            navHostController = navController,
        )

        // Test Full Loading
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.loading_massage)).check(matches(isDisplayed()))
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))

        // Test Error Full Page Network
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultErrorNetwork)
        }
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.error_message), withText(R.string.error_message_network)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Test Error Full Page Server
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultErrorServer)
        }
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.error_message), withText(R.string.error_message_server)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Test Error Full Page Client
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultErrorClient)
        }
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.error_message), withText(R.string.error_message_other)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Test Error Empty Page
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getSearchedPhotoUseCase.emitResult(resultErrorEmpty)
        }
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.error_message), withText(R.string.error_message_empty)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry_button))
            .check(matches(isNotDisplayed()))

        onView(allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.search_bar))))
            .check(matches(isDisplayed())).perform(click())
        assertThat(navController.currentDestination?.id).isNull()

        activityScenario.recreate()
    }
}