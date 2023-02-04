package com.ngengs.android.app.dailyimage.presenter.fragment.favorite

import android.view.View
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.di.FakeUseCaseModule
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.CLIENT
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.NETWORK
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.SERVER
import com.ngengs.android.app.dailyimage.helpers.PhotoDataCreator
import com.ngengs.android.app.dailyimage.helpers.espresso.RecyclerViewChildAction
import com.ngengs.android.app.dailyimage.helpers.espresso.RecyclerViewChildMatcher.atPosition
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewMatcher.isNotDisplayed
import com.ngengs.android.app.dailyimage.helpers.launchCoroutine
import com.ngengs.android.app.dailyimage.launchFragmentInHiltContainer
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragmentTest
import com.ngengs.android.libs.test.utils.ext.shouldBe
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Test

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
@HiltAndroidTest
class FavoriteFragmentTest : BaseFragmentTest() {
    @Test
    fun test_correctlyRenderData() {
        val mockData1 = PhotoDataCreator.createList(forge, 20)
        val resultSuccess1 = Results.Success(CompletableData(false, mockData1))

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        val activityScenario = launchFragmentInHiltContainer<FavoriteFragment>(
            navHostController = navController,
            navCurrentDestination = R.id.homeFragment,
        )
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(Results.Loading())
        }
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))

        // Test Display Data
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(resultSuccess1)
        }
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(isNotDisplayed()))
        onView(withId(R.id.rv))
            .check(matches(isDisplayed()))
            .check(
                matches(atPosition(0, hasDescendant(withText(R.string.favorite_images)))),
            )
            .check(
                matches(
                    atPosition(1, hasDescendant(withText(mockData1.first().user!!.name))),
                ),
            )
            .perform(
                scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(mockData1.last().user!!.name)),
                ),
            )
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        navController.currentDestination?.id shouldBe R.id.detailFragment
        navController.popBackStack()

        onView(withId(R.id.rv))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(
                RecyclerViewChildAction.recyclerChildAction<View>(R.id.view_type_button) {
                    performClick()
                },
            )
            .check(
                matches(atPosition(1, hasDescendant(withText(mockData1.first().user!!.name)))),
            )

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

        val activityScenario = launchFragmentInHiltContainer<FavoriteFragment>(
            navHostController = navController,
            navCurrentDestination = R.id.homeFragment,
        )

        // Test Full Loading
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.loading_massage)).check(matches(isDisplayed()))
        onView(withId(R.id.rv)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_image)).check(matches(isNotDisplayed()))
        onView(withId(R.id.error_message)).check(matches(isNotDisplayed()))

        // Test Error Full Page Network
        activityScenario.launchCoroutine {
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(resultErrorNetwork)
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
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(resultErrorServer)
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
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(resultErrorClient)
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
            FakeUseCaseModule.useCase.getFavoriteListUseCase.emit(resultErrorEmpty)
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