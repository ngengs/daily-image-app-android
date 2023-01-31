package com.ngengs.android.app.dailyimage.utils.common.ext

import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeNull
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommonExtKtTest {
    @get:Rule
    val forge = ForgeRule()

    @Test
    fun debugTryReturnSucceedValue() {
        // Given
        val succeedResult = forge.anAlphaNumericalString(size = 20)
        // When
        val result = debugTry { succeedResult }
        // Then
        result shouldBe succeedResult
    }

    @Test
    fun debugTryReturnFailedValue() {
        // When
        val result = debugTry<String> { throw Exception(forge.aNumericalString(size = 20)) }
        // Then
        result.shouldBeNull()
    }

    @Test
    fun debugTrySuspendReturnSucceedValue() = runTest {
        // Given
        val succeedResult = forge.anAlphaNumericalString(size = 20)
        // When
        val result = debugTrySuspend {
            withContext(this.coroutineContext) {
                delay(500)
                succeedResult
            }
        }
        // Then
        result shouldBe succeedResult
    }

    @Test
    fun debugTrySuspendReturnFailedValue() = runTest {
        // When
        val result = debugTrySuspend {
            withContext<String>(this.coroutineContext) {
                delay(500)
                throw Exception(forge.aNumericalString(size = 20))
            }
        }
        // Then
        result.shouldBeNull()
    }
}