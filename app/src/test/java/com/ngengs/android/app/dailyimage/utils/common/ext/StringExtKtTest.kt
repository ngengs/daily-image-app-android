package com.ngengs.android.app.dailyimage.utils.common.ext

import com.ngengs.android.libs.test.utils.ext.shouldBe
import org.junit.Test

class StringExtKtTest {
    @Test
    fun test_toCapitalize() {
        // Given
        val text = "somethingtextlower"
        // When
        val result = text.toCapitalize()
        // Then
        result shouldBe "Somethingtextlower"
    }

    @Test
    fun test_toTitleCase() {
        // Given
        val text = "something text lower"
        // When
        val result = text.toTitleCase()
        // Then
        result shouldBe "Something Text Lower"
    }
}