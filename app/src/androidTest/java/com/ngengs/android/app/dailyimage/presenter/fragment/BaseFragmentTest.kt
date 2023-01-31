package com.ngengs.android.app.dailyimage.presenter.fragment

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.ngengs.android.app.dailyimage.di.FakeUseCaseModule
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import dagger.hilt.android.testing.HiltAndroidRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseFragmentTest {
    @get:Rule(order = 2)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 0)
    val coroutineRule = CoroutineRule()

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val forge = ForgeRule()

    @Before
    fun setupParent() {
        FakeUseCaseModule.init()
        hiltRule.inject()
        setUp()
    }

    @After
    fun tearDownParent() {
        FakeUseCaseModule.tearDown()
        tearDown()
    }

    protected open fun setUp() = Unit
    protected open fun tearDown() = Unit

    val testContext: Context get() = ApplicationProvider.getApplicationContext()

    companion object {
        @AfterClass
        fun tearDownClass() {
            clearAllMocks()
            unmockkAll()
        }
    }
}