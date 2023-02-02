package com.ngengs.android.app.dailyimage.helpers.espresso

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
@Suppress("unused")
object ViewPager2Matcher {
    fun withCurrentItem(item: Int): Matcher<View> {
        return WithCurrentItemMatcher(item)
    }

    fun withPositionInViewPager2(@IdRes viewPagerId: Int, position: Int): Matcher<View> {
        return WithPositionInViewPager2Matcher(viewPagerId, position)
    }

    private class WithCurrentItemMatcher(private val item: Int) :
        BoundedMatcher<View, ViewPager2>(ViewPager2::class.java) {

        override fun describeTo(description: Description) {
            description.appendText("ViewPager2 with current item $item")
        }

        override fun matchesSafely(viewPager: ViewPager2): Boolean {
            return viewPager.currentItem == item
        }
    }

    private class WithPositionInViewPager2Matcher(
        @IdRes private val id: Int,
        private val position: Int
    ) : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description) {
            description.appendText("with position $position in ViewPager2 which has id $id")
        }

        override fun matchesSafely(item: View): Boolean {
            val parent = item.parent as? RecyclerView
                ?: return false

            val grandParent = parent.parent as? ViewPager2
                ?: return false

            if (grandParent.id != id)
                return false

            val viewHolder = parent.findViewHolderForAdapterPosition(position)
                ?: return false

            return item == viewHolder.itemView
        }
    }
}