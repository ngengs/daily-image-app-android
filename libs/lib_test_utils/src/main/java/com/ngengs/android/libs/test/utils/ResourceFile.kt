package com.ngengs.android.libs.test.utils

import java.io.File

/**
 * Created by rizky.kharisma on 29/12/22.
 * @ngengs
 */
object ResourceFile{
    fun getJson(path: String): String {
        // Load the JSON response
        val uri = this.javaClass.classLoader?.getResource("json/$path")
        val file = uri?.path?.let { File(it) }
        return file?.let { String(it.readBytes()) }.orEmpty()
    }



}