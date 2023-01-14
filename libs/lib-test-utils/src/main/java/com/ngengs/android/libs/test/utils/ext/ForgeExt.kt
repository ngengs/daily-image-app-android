package com.ngengs.android.libs.test.utils.ext

import fr.xgouchet.elmyr.Case
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.jvm.factories.DateForgeryFactory
import fr.xgouchet.elmyr.jvm.factories.UriForgeryFactory
import fr.xgouchet.elmyr.jvm.factories.UrlForgeryFactory
import java.net.URI
import java.net.URL
import java.util.Date

/**
 * Created by rizky.kharisma on 29/12/22.
 * @ngengs
 */
fun Forge.aSentence(case: Case, size: Int = -1): String {
    val length = if (size < 5) anInt(min = 5) else size
    return (1..length).joinToString(" ") { anAlphabeticalString(case) }
}

fun Forge.aDate(): Date {
    return DateForgeryFactory().getForgery(this)
}

fun Forge.anUri(): URI {
    return UriForgeryFactory().getForgery(this)
}

fun Forge.anUrl(): URL {
    return UrlForgeryFactory().getForgery(this)
}