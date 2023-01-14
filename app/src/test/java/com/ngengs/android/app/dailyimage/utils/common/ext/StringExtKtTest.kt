package com.ngengs.android.app.dailyimage.utils.common.ext

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringExtKtTest {
    @Test
    fun test_toCapitalize() {
        // Given
        val text = "somethingtextlower"
        // When
        val result = text.toCapitalize()
        // Then
        assertThat(result).isEqualTo("Somethingtextlower")
    }

    @Test
    fun test_toTitleCase() {
        // Given
        val text = "something text lower"
        // When
        val result = text.toTitleCase()
        // Then
        assertThat(result).isEqualTo("Something Text Lower")
    }
}