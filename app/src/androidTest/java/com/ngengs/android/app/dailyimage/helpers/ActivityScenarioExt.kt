package com.ngengs.android.app.dailyimage.helpers

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ActivityScenario
import com.ngengs.android.app.dailyimage.FRAGMENT_TEST_TAG
import com.ngengs.android.app.dailyimage.presenter.activity.HiltTestActivity
import kotlinx.coroutines.launch
import kotlin.reflect.full.cast

fun ActivityScenario<HiltTestActivity>.launchCoroutine(action: suspend (Fragment) -> Unit) {
    onActivity {
        val fragment = requireNotNull(
            it.supportFragmentManager.findFragmentByTag(FRAGMENT_TEST_TAG)
        )
        fragment.lifecycleScope.launch {
            action.invoke(fragment)
        }
    }
}

inline fun <reified F : Fragment> ActivityScenario<HiltTestActivity>.onFragment(
    crossinline action: (F) -> Unit
) {
    onActivity {
        val fragment = requireNotNull(
            it.supportFragmentManager.findFragmentByTag(FRAGMENT_TEST_TAG)
        )
        val castFragment = F::class.cast(fragment)
        action.invoke(castFragment)
    }
}
