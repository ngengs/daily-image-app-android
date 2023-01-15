package com.ngengs.android.app.dailyimage.presenter.fragment.home

import android.view.View
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
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
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.fragment.search.SearchFragmentArgs
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Test

@HiltAndroidTest
class HomeFragmentTest : BaseFragmentTest() {
    @Test
    fun test_correctlyRenderData() {
        val mockCache = PhotoDataCreator.createList(forge, 5)
        val mockData1 = PhotoDataCreator.createList(forge, 20)
        val mockData2 = PhotoDataCreator.createList(forge, 10, startAt = 21)
        val resultCache = Results.Success(CompletableCachedData(true, mockCache, isCache = true))
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
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        val activityScenario = launchFragmentInHiltContainer<HomeFragment>(
            navHostController = navController,
            navCurrentDestination = R.id.homeFragment
        )
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(Results.Loading())
        }

        // Test Cache Displayed
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultCache)
        }
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.rv))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    atPosition(0, hasDescendant(withText(R.string.latest_images)))
                )
            )
            .check(
                matches(
                    atPosition(1, hasDescendant(withText(R.string.loading_refresh_data)))
                )
            )
            .check(
                matches(
                    atPosition(2, hasDescendant(withText(mockCache.first().user!!.name)))
                )
            )
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click())
            )
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.detailFragment)
        navController.popBackStack()

        // Test Display Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultSuccess1)
        }
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.rv))
            .check(matches(isDisplayed()))
            .check(
                matches(
                    atPosition(0, hasDescendant(withText(R.string.latest_images)))
                )
            )
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

        // Test Pagination Loading
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultLoadingNextPage)
        }
        onView(withId(R.id.rv))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

        // Test Display Next Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultSuccess2)
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
            .perform(
                recyclerChildAction<View>(R.id.order_type_button) {
                    performClick()
                }
            )

        // Test Changed Type Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultSuccess2)
        }
        onView(withId(R.id.rv))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(0, hasDescendant(withText(R.string.popular_images)))
                )
            )
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

        // Test Error in Next Page
        activityScenario.onFragment<HomeFragment> {
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
        ).check(matches(isDisplayed())).perform(typeText("Apple"))
        onView(withId(R.id.rv_suggestion)).check(matches(isDisplayed()))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(mockSuggestion1.last())))
            )
        Thread.sleep(400L) // Delay before navigation for waiting search closed
        onView(withText(mockSuggestion1.last())).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.searchFragment)
        val currentBundle1 = navController.backStack.last().arguments
        val safeArg1 = SearchFragmentArgs.fromBundle(currentBundle1!!)
        assertThat(safeArg1.searchText).isEqualTo(mockSuggestion1.last())
        navController.popBackStack()

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
        searchViewEditor2.check(matches(isDisplayed())).perform(typeText("Orange"))
        onView(withId(R.id.rv_suggestion)).check(matches(isDisplayed()))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(mockSuggestion2.last())))
            )
        searchViewEditor2.perform(pressImeActionButton())
        Thread.sleep(400L) // Delay before navigation for waiting search closed
        val currentBundle2 = navController.backStack.last().arguments
        val safeArg2 = SearchFragmentArgs.fromBundle(currentBundle2!!)
        assertThat(safeArg2.searchText).isEqualTo("Orange")
        navController.popBackStack()

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

        val activityScenario = launchFragmentInHiltContainer<HomeFragment>(
            navHostController = navController,
            navCurrentDestination = R.id.homeFragment
        )

        // Test Full Loading
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.loading_massage)).check(matches(isDisplayed()))
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))

        // Test Error Full Page Network
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorNetwork)
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
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorServer)
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
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorClient)
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
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorEmpty)
        }
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.error_message), withText(R.string.error_message_empty)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry_button))
            .check(matches(isNotDisplayed()))

        activityScenario.recreate()
    }
}