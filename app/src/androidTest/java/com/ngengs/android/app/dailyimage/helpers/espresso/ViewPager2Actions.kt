package com.ngengs.android.app.dailyimage.helpers.espresso

import android.view.View
import androidx.annotation.Nullable
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.hamcrest.Matcher

/**
 * Espresso actions for interacting with a {@link ViewPager2}.
 * <p>
 * The implementation of this class has been copied over from
 * <a href="https://github.com/android/android-test/blob/master/espresso/contrib/java/androidx/test/espresso/contrib/ViewPagerActions.java">ViewPagerActions.java</a>
 * with a small number of modifications to make it work for
 * {@link ViewPager2} instead of
 * <a href="https://developer.android.com/reference/kotlin/androidx/viewpager/widget/ViewPager">ViewPager</a>.
 * <p>
 * I have created an issue in the ViewPager2 IssueTracker space
 * <a href="https://issuetracker.google.com/issues/207785217">here</a>
 * requesting for this class to be added in a {@code viewpager2-testing} library within the
 * <a href="https://maven.google.com/web/index.html#androidx.viewpager2">androidx.viewpager2</a>
 * group.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ViewPager2Actions {
    private const val DEFAULT_SMOOTH_SCROLL = false

    /**
     * Moves [ViewPager2] to the right by one page.
     */
    fun scrollRight(): ViewAction {
        return scrollRight(DEFAULT_SMOOTH_SCROLL)
    }

    /**
     * Moves [ViewPager2] to the right by one page.
     */
    fun scrollRight(smoothScroll: Boolean): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String = "ViewPager2 move one page to the right"
            override fun performScroll(viewPager: ViewPager2) {
                val current = viewPager.currentItem
                viewPager.setCurrentItem(current + 1, smoothScroll)
            }
        }
    }

    /**
     * Moves [ViewPager2] to the left be one page.
     */
    fun scrollLeft(): ViewAction {
        return scrollLeft(DEFAULT_SMOOTH_SCROLL)
    }

    /**
     * Moves [ViewPager2] to the left by one page.
     */
    fun scrollLeft(smoothScroll: Boolean): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String = "ViewPager2 move one page to the left"
            override fun performScroll(viewPager: ViewPager2) {
                val current = viewPager.currentItem
                viewPager.setCurrentItem(current - 1, smoothScroll)
            }
        }
    }

    /**
     * Moves [ViewPager2] to the last page.
     */
    fun scrollToLast(): ViewAction {
        return scrollToLast(DEFAULT_SMOOTH_SCROLL)
    }

    /**
     * Moves [ViewPager2] to the last page.
     */
    fun scrollToLast(smoothScroll: Boolean): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String = "ViewPager2 move to last page"
            override fun performScroll(viewPager: ViewPager2) {
                val size = viewPager.adapter?.itemCount ?: 0
                if (size > 0) {
                    viewPager.setCurrentItem(size - 1, smoothScroll)
                }
            }
        }
    }

    /**
     * Moves [ViewPager2] to the first page.
     */
    fun scrollToFirst(): ViewAction {
        return scrollToFirst(DEFAULT_SMOOTH_SCROLL)
    }

    /**
     * Moves [ViewPager2] to the first page.
     */
    fun scrollToFirst(smoothScroll: Boolean): ViewAction {
        return object : ViewPagerScrollAction() {

            override fun getDescription(): String = "ViewPager2 move to first page"
            override fun performScroll(viewPager: ViewPager2) {
                val size = viewPager.adapter?.itemCount ?: 0
                if (size > 0) {
                    viewPager.setCurrentItem(0, smoothScroll)
                }
            }
        }
    }

    /**
     * Moves [ViewPager2] to a specific page.
     */
    fun scrollToPage(page: Int): ViewAction {
        return scrollToPage(page, DEFAULT_SMOOTH_SCROLL)
    }

    /**
     * Moves [ViewPager2] to specific page.
     */
    fun scrollToPage(page: Int, smoothScroll: Boolean): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String = "ViewPager2 move to page"
            override fun performScroll(viewPager: ViewPager2) {
                viewPager.setCurrentItem(page, smoothScroll)
            }
        }
    }

    private class CustomViewPager2Listener :
        OnPageChangeCallback(),
        IdlingResource {

        private var mCurrState = ViewPager2.SCROLL_STATE_IDLE

        @Nullable
        private var mCallback: ResourceCallback? = null
        var mNeedsIdle = false
        override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
            mCallback = resourceCallback
        }

        override fun getName(): String {
            return "ViewPager2 listener"
        }

        override fun isIdleNow(): Boolean {
            return if (!mNeedsIdle) {
                true
            } else {
                mCurrState == ViewPager2.SCROLL_STATE_IDLE
            }
        }

        override fun onPageSelected(position: Int) {
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mCurrState = state
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }
    }

    private abstract class ViewPagerScrollAction : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayed()
        }

        override fun perform(uiController: UiController, view: View) {
            val viewPager = view as ViewPager2

            // Add a custom tracker listener
            val customListener = CustomViewPager2Listener()
            viewPager.registerOnPageChangeCallback(customListener)

            // Note that we're running the following block in a try-finally construct.
            // This is needed since some of the actions are going to throw (expected) exceptions.
            // If that happens, we still need to clean up after ourselves
            // to leave the system (Espresso) in a good state.
            try {
                // Register our listener as idling resource so that Espresso waits until the
                // wrapped action results in the ViewPager2 getting to the SCROLL_STATE_IDLE state
                IdlingRegistry.getInstance().register(customListener)
                uiController.loopMainThreadUntilIdle()
                performScroll(viewPager)
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = true
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = false
            } finally {
                // Unregister our idling resource
                IdlingRegistry.getInstance().unregister(customListener)
                // And remove our tracker listener from ViewPager2
                viewPager.unregisterOnPageChangeCallback(customListener)
            }
        }

        protected abstract fun performScroll(viewPager: ViewPager2)
    }
}