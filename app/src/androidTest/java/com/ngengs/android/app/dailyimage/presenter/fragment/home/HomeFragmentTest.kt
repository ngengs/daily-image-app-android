package com.ngengs.android.app.dailyimage.presenter.fragment.home

import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewPager2Actions
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewPager2Matcher.withPositionInViewPager2
import com.ngengs.android.app.dailyimage.helpers.launchCoroutine
import com.ngengs.android.app.dailyimage.launchFragmentInHiltContainer
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragmentTest
import com.ngengs.android.app.dailyimage.presenter.fragment.search.SearchFragmentArgs
import com.ngengs.android.libs.test.utils.ext.shouldBe
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Test
import java.util.EmptyStackException

@HiltAndroidTest
class HomeFragmentTest : BaseFragmentTest() {
    @Test
    fun test_correctlyRenderData() {
        val mockCache = PhotoDataCreator.createList(forge, 5)
        val mockData1 = PhotoDataCreator.createList(forge, 20)
        val mockData2 = PhotoDataCreator.createList(forge, 10, startAt = 21)
        val mockFavoriteData = PhotoDataCreator.createList(forge, 2)
        val resultCache = Results.Success(CompletableCachedData(true, mockCache, isCache = true))
        val resultSuccess1 = Results.Success(CompletableCachedData(false, mockData1))
        val resultSuccess2 =
            Results.Success(CompletableCachedData(true, mockData1 + mockData2))
        val resultLoadingNextPage =
            Results.Loading(oldData = CompletableCachedData(false, mockData1))

        val mockSuggestion1 = (1..10).map { forge.anAlphaNumericalString(size = it + 5) }
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
        val rv = onViewPagerPosition(R.id.rv)
        val errorImageView = onViewPagerPosition(R.id.error_image)
        val errorMessageView = onViewPagerPosition(R.id.error_message)
        val loadingView = onViewPagerPosition(R.id.loading_indicator)
        errorImageView.check(matches(isNotDisplayed()))
        errorMessageView.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        rv.check(matches(isCompletelyDisplayed()))
            .check(matches(atPosition(0, hasDescendant(withText(R.string.latest_images)))))
            .check(matches(atPosition(1, hasDescendant(withText(R.string.loading_refresh_data)))))
            .check(
                matches(atPosition(2, hasDescendant(withText(mockCache.first().user!!.name))))
            )
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click())
            )
        navController.currentDestination?.id shouldBe R.id.detailFragment
        navController.popBackStack()

        // Test Display Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultSuccess1)
        }
        errorImageView.check(matches(isNotDisplayed()))
        errorMessageView.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        rv.check(matches(isCompletelyDisplayed()))
            .check(matches(atPosition(0, hasDescendant(withText(R.string.latest_images)))))
            .check(
                matches(atPosition(1, hasDescendant(withText(mockData1.first().user!!.name))))
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
        rv.perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

        // Test Display Next Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultSuccess2)
        }
        rv.perform(scrollToPosition<RecyclerView.ViewHolder>(0))
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
        rv.perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(withText(R.string.popular_images)))))
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


        rv.perform(scrollToPosition<RecyclerView.ViewHolder>(resultSuccess2.data.data.size))
        onView(
            allOf(withText(R.string.tab_title_gallery), isDescendantOfA(withId(R.id.tab_layout)))
        ).perform(click())

        // Test Tab
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(mockFavoriteData)
        }
        val rvFavorite = onViewPagerPosition(R.id.rv, position = 1)
        onView(withId(R.id.view_pager)).perform(ViewPager2Actions.scrollRight())
        rvFavorite.check(matches(isCompletelyDisplayed()))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(withText(R.string.favorite_images)))))

        onView(
            allOf(withText(R.string.tab_title_favorite), isDescendantOfA(withId(R.id.tab_layout)))
        ).perform(click())
        onView(withId(R.id.view_pager)).perform(ViewPager2Actions.scrollLeft())
        rv.check(matches(isCompletelyDisplayed()))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(withText(R.string.popular_images)))))

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
        onView(withText(mockSuggestion1.last())).perform(click())
        Thread.sleep(400L) // Delay before navigation for waiting search closed
        navController.currentDestination?.id shouldBe R.id.searchFragment
        val currentBundle1 = navController.backStack.last().arguments
        val safeArg1 = SearchFragmentArgs.fromBundle(currentBundle1!!)
        safeArg1.searchText shouldBe mockSuggestion1.last()
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
        val rv = onViewPagerPosition(R.id.rv)
        val errorImageView = onViewPagerPosition(R.id.error_image)
        val errorMessageView = onViewPagerPosition(R.id.error_message)
        val errorRetryView = onViewPagerPosition(R.id.retry_button)
        val loadingView = onViewPagerPosition(R.id.loading_indicator)
        val loadingMessageView = onViewPagerPosition(R.id.loading_massage)

        // Test Full Loading
        loadingView.check(matches(isDisplayed()))
        loadingMessageView.check(matches(isDisplayed()))
        rv.check(matches(isNotDisplayed()))
        errorImageView.check(matches(isNotDisplayed()))
        errorMessageView.check(matches(isNotDisplayed()))

        // Test Error Full Page Network
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorNetwork)
        }
        rv.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        errorImageView.check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.error_message),
                withText(R.string.error_message_network),
                isDescendantOfA(withPositionInViewPager2(R.id.view_pager, 0))
            )
        ).check(matches(isDisplayed()))
        errorRetryView.check(matches(isDisplayed())).perform(click())

        // Test Error Full Page Server
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorServer)
        }
        rv.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        errorImageView.check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.error_message),
                withText(R.string.error_message_server),
                isDescendantOfA(withPositionInViewPager2(R.id.view_pager, 0))
            )
        ).check(matches(isDisplayed()))
        errorRetryView.check(matches(isDisplayed())).perform(click())

        // Test Error Full Page Client
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorClient)
        }
        rv.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        errorImageView.check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.error_message),
                withText(R.string.error_message_other),
                isDescendantOfA(withPositionInViewPager2(R.id.view_pager, 0))
            )
        ).check(matches(isDisplayed()))
        errorRetryView.check(matches(isDisplayed())).perform(click())

        // Test Error Empty Page
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getPhotoListUseCase.emitResult(resultErrorEmpty)
        }
        rv.check(matches(isNotDisplayed()))
        loadingView.check(matches(isNotDisplayed()))
        errorImageView.check(matches(isDisplayed()))
        onView(
            allOf(
                allOf(
                    withId(R.id.error_message),
                    withText(R.string.error_message_empty),
                    isDescendantOfA(withPositionInViewPager2(R.id.view_pager, 0))
                )
            )
        ).check(matches(isDisplayed()))
        errorRetryView.check(matches(isNotDisplayed()))

        // Test Tab
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getFavoriteListUseCase
                .emit(Results.Failure(EmptyStackException(), EMPTY))
        }
        val rvFavorite = onViewPagerPosition(R.id.rv, position = 1)
        onView(withId(R.id.view_pager)).perform(ViewPager2Actions.scrollRight())
        rvFavorite.check(matches(isNotDisplayed()))
        onView(
            allOf(
                allOf(
                    withId(R.id.error_message),
                    withText(R.string.error_message_empty),
                    isDescendantOfA(withPositionInViewPager2(R.id.view_pager, 1))
                )
            )
        ).check(matches(isDisplayed()))

        activityScenario.recreate()
    }

    private fun onViewPagerPosition(@IdRes id: Int, position: Int = 0) = onView(
        allOf(
            withId(id),
            isDescendantOfA(withPositionInViewPager2(R.id.view_pager, position))
        )
    )
}