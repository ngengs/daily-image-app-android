package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import android.widget.ImageButton
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.helpers.PhotoDataCreator
import com.ngengs.android.app.dailyimage.launchFragmentInHiltContainer
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragmentTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.junit.Test

@HiltAndroidTest
class DetailFragmentTest : BaseFragmentTest() {
    @Test
    fun test_correctlyRenderData() {
        val mockData = PhotoDataCreator.create(forge, 0)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        val args = DetailFragmentArgs(photo = mockData)

        val activityScenario = launchFragmentInHiltContainer<DetailFragment>(
            fragmentArgs = args.toBundle(),
            navHostController = navController,
        )

        onView(withId(R.id.photo)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.full_name), withText(mockData.user!!.name)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.username), withText("@${mockData.user!!.username}")))
            .check(matches(isDisplayed()))
        if (mockData.description != null) {
            onView(allOf(withId(R.id.description), withText(mockData.description)))
                .check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.description)).check(matches(not(isDisplayed())))
        }
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

        Thread.sleep(200L) // Delay wait image loaded
        onView(withId(R.id.photo)).perform(click())
        Thread.sleep(200L) // Delay touch action
        onView(withId(R.id.toolbar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.full_name)).check(matches(not(isDisplayed())))
        onView(withId(R.id.username)).check(matches(not(isDisplayed())))
        onView(withId(R.id.description)).check(matches(not(isDisplayed())))

        onView(withId(R.id.photo)).perform(click())
        Thread.sleep(200L) // Delay touch action
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.full_name)).check(matches(isDisplayed()))
        onView(withId(R.id.username)).check(matches(isDisplayed()))

        onView(allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(isDisplayed())).perform(click())
        assertThat(navController.currentDestination?.id).isNull()

        activityScenario.recreate()
    }
}