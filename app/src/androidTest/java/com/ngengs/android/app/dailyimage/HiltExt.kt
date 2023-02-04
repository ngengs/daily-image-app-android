package com.ngengs.android.app.dailyimage

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.ngengs.android.app.dailyimage.presenter.activity.HiltTestActivity

/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int =
        androidx.fragment.testing.R.style.FragmentScenarioEmptyFragmentActivityTheme,
    navHostController: TestNavHostController? = null,
    @IdRes navCurrentDestination: Int? = null,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    crossinline action: T.() -> Unit = {},
): ActivityScenario<HiltTestActivity> {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java,
        ),
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity." +
            "THEME_EXTRAS_BUNDLE_KEY",
        themeResId,
    )
    Log.d("FragmentInHiltContainer", "Initialize")

    return ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name,
        )
        fragment.arguments = fragmentArgs
        fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
            if (viewLifecycleOwner != null) {
                Log.d("FragmentInHiltContainer", "Preparing NavController setup")
                navHostController?.let {
                    if (navCurrentDestination != null) {
                        it.setCurrentDestination(navCurrentDestination, fragmentArgs ?: Bundle())
                    }
                    Navigation.setViewNavController(fragment.requireView(), it)
                    Log.d("FragmentInHiltContainer", "NavController setup finish")
                }
            }
        }
        activity.supportFragmentManager.commitNow {
            add(android.R.id.content, fragment, FRAGMENT_TEST_TAG)
            setMaxLifecycle(fragment, initialState)
        }

        action.invoke(fragment as T)
    }
}

const val FRAGMENT_TEST_TAG = "HiltFragmentScenario_Fragment_TAG"