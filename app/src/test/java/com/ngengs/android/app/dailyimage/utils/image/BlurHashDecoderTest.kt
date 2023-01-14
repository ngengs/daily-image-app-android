package com.ngengs.android.app.dailyimage.utils.image

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.utils.image.BlurHashDecoder.clearCache
import com.ngengs.android.app.dailyimage.utils.image.BlurHashDecoder.decode
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
@Config(sdk = [32])
class BlurHashDecoderTest {
    @Before
    @Throws(Exception::class)
    fun setUp() {
        clearCache()
    }

    @Test
    fun decode_smallImage_cacheEnabled_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    @Test
    fun decode_smallImage_differentCache_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12, useCache = false)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    @Test
    fun decode_smallImage_cacheDisabled_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12, useCache = false)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12, useCache = false)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 20, 12, useCache = false)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    @Test
    fun decode_bigImage_cacheEnabled_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    @Test
    fun decode_bigImage_differentCache_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100, useCache = false)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    @Test
    fun decode_bigImage_cacheDisabled_shouldGetTheSameBitmapInManyRequests() {
        val bmp1 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100, useCache = false)!!
        val bmp2 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100, useCache = false)!!
        val bmp3 = decode("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100, useCache = false)!!

        bmp1.assertEquals(bmp2)
        bmp2.assertEquals(bmp3)
    }

    private fun Bitmap.assertEquals(bitmap2: Bitmap) {
        val buffer1: ByteBuffer = ByteBuffer.allocate(height * rowBytes)
        copyPixelsToBuffer(buffer1)
        val buffer2: ByteBuffer = ByteBuffer.allocate(bitmap2.height * bitmap2.rowBytes)
        bitmap2.copyPixelsToBuffer(buffer2)
        assertThat(buffer1.array()).isEqualTo(buffer2.array())
    }
}