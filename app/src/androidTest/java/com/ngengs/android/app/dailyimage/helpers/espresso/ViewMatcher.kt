package com.ngengs.android.app.dailyimage.helpers.espresso

import android.view.View
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not

@Suppress("unused")
object ViewMatcher {
    fun isNotDisplayed(): Matcher<View> = not(isDisplayed())

    fun isNotCompleteDisplayed(): Matcher<View> = not(isCompletelyDisplayed())

    fun withItemHint(hintText: String): Matcher<View> = withItemHint(`is`(hintText))

    fun withItemHint(matcherText: Matcher<String>): Matcher<View> {
        return object : BoundedMatcher<View, EditText>(EditText::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with item hint: $matcherText")
            }

            override fun matchesSafely(editTextField: EditText): Boolean {
                return matcherText.matches(editTextField.hint.toString())
            }
        }
    }

    fun withItemHint(@StringRes resId: Int): Matcher<View> {
        return object : BoundedMatcher<View, EditText>(EditText::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with item hint: $resId")
            }

            override fun matchesSafely(editTextField: EditText): Boolean {
                val hintText = editTextField.resources.getString(resId)
                return editTextField.hint.toString() == hintText
            }
        }
    }
}