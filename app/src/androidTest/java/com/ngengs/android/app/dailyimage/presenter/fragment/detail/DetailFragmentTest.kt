package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import android.widget.ImageButton
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.di.FakeUseCaseModule
import com.ngengs.android.app.dailyimage.helpers.PhotoDataCreator
import com.ngengs.android.app.dailyimage.helpers.espresso.ViewMatcher.isNotDisplayed
import com.ngengs.android.app.dailyimage.helpers.espresso.ZoomGestureViewAction.pinchOut
import com.ngengs.android.app.dailyimage.launchFragmentInHiltContainer
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragmentTest
import com.ngengs.android.app.dailyimage.presenter.fragment.detail.DetailFragmentImpl.Companion.FAB_FAVORITE_TAG
import com.ngengs.android.libs.test.utils.ext.shouldBe
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.Test

@HiltAndroidTest
class DetailFragmentTest : BaseFragmentTest() {
    private lateinit var idlingResource: IdlingResource

    override fun tearDown() {
        super.tearDown()
        if (this::idlingResource.isInitialized) {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }

    @Test
    fun test_correctlyRenderData() {
        val mockData = PhotoDataCreator.create(forge, 0)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        val args = DetailFragmentArgs(photo = mockData)

        val activityScenario = launchFragmentInHiltContainer<DetailFragment>(
            fragmentArgs = args.toBundle(),
            navHostController = navController,
            navCurrentDestination = R.id.detailFragment,
        ) {
            idlingResource = getIdlingResource()
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withId(R.id.photo)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.full_name), withText(mockData.user!!.name)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.username), withText("@${mockData.user!!.username}")))
            .check(matches(isDisplayed()))
        if (mockData.description != null) {
            onView(allOf(withId(R.id.description), withText(mockData.description)))
                .check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.description)).check(matches(isNotDisplayed()))
        }
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

        onView(withId(R.id.photo)).perform(click())
        Thread.sleep(100L) // Delay touch action
        onView(withId(R.id.toolbar)).check(matches(isNotDisplayed()))
        onView(withId(R.id.full_name)).check(matches(isNotDisplayed()))
        onView(withId(R.id.username)).check(matches(isNotDisplayed()))
        onView(withId(R.id.description)).check(matches(isNotDisplayed()))

        onView(withId(R.id.photo)).perform(click())
        Thread.sleep(100L) // Delay touch action
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.full_name)).check(matches(isDisplayed()))
        onView(withId(R.id.username)).check(matches(isDisplayed()))

        onView(withId(R.id.favorite_button)).check(matches(isDisplayed())).perform(click())
        onView(
            allOf(withId(R.id.favorite_button), withTagValue(`is`("${FAB_FAVORITE_TAG}true"))),
        ).check(matches(isDisplayed()))
        FakeUseCaseModule.useCase.changeFavoriteStatusUseCase.status shouldBe true
        onView(withId(R.id.favorite_button)).check(matches(isDisplayed())).perform(click())
        onView(
            allOf(withId(R.id.favorite_button), withTagValue(`is`("${FAB_FAVORITE_TAG}false"))),
        ).check(matches(isDisplayed()))
        FakeUseCaseModule.useCase.changeFavoriteStatusUseCase.status shouldBe false

        onView(withId(R.id.photo)).perform(pinchOut())
        onView(withId(R.id.toolbar)).check(matches(isNotDisplayed()))
        onView(withId(R.id.full_name)).check(matches(isNotDisplayed()))
        onView(withId(R.id.username)).check(matches(isNotDisplayed()))
        onView(withId(R.id.description)).check(matches(isNotDisplayed()))

        onView(withId(R.id.photo)).perform(click())
        Thread.sleep(100L) // Delay touch action
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.full_name)).check(matches(isDisplayed()))
        onView(withId(R.id.username)).check(matches(isDisplayed()))

        onView(allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(isDisplayed())).perform(click())
        navController.currentDestination?.id shouldBe R.id.homeFragment

        activityScenario.recreate()
    }
}