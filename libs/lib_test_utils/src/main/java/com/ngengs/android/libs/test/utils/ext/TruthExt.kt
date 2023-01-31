package com.ngengs.android.libs.test.utils.ext

import com.google.common.truth.Truth.assertThat
import kotlin.reflect.KClass

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */

infix fun <T> T?.shouldBe(expected: Any?) {
    startAssert().isEqualTo(expected)
}

infix fun <T> T?.shouldNot(unExpected: Any?) {
    startAssert().isNotEqualTo(unExpected)
}

infix fun <T> T?.shouldInstanceOf(expected: KClass<*>) {
    startAssert().isInstanceOf(expected.java)
}

infix fun <T> T?.shouldNotInstanceOf(expected: KClass<*>) {
    startAssert().isNotInstanceOf(expected.java)
}

infix fun <T> T?.shouldInstanceOf(expected: Class<*>) {
    startAssert().isInstanceOf(expected)
}

infix fun <T> T?.shouldNotInstanceOf(expected: Class<*>) {
    startAssert().isNotInstanceOf(expected)
}

fun Boolean?.shouldBeTrue() {
    assertThat(this).isTrue()
}

fun Boolean?.shouldBeFalse() {
    assertThat(this).isFalse()
}

fun <T> T?.shouldBeNull() {
    startAssert().isNull()
}

fun <T> T?.shouldNotNull() {
    startAssert().isNotNull()
}

fun Iterable<*>.shouldBeEmpty() {
    startAssert().isEmpty()
}

fun Iterable<*>.shouldNotEmpty() {
    startAssert().isNotEmpty()
}

infix fun Iterable<*>.shouldHasSize(expectedSize: Int) {
    startAssert().hasSize(expectedSize)
}

fun <T> T?.startAssert() = assertThat(this)
fun Iterable<*>.startAssert() = assertThat(this)