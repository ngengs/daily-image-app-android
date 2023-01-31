package com.ngengs.android.app.dailyimage.utils.image

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
import fr.xgouchet.elmyr.junit4.ForgeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [32])
class ImageOptionsTest {
    private lateinit var imageOptions: ImageOptions

    @get:Rule
    val forge = ForgeRule()

    @Before
    fun setUp() {
        imageOptions = ImageOptions()
    }

    @Test
    fun test_toRequestOptions_correctlyTransform() {
        // Given
        imageOptions.imageOnLoading = R.drawable.ic_baseline_hide_image_24
        imageOptions.imageOnFail = R.drawable.ic_baseline_arrow_back_24
        imageOptions.onlyRetrieveFromCache = true
        imageOptions.resize = ImageOptions.Size(width = 100, height = 200)
        imageOptions.centerCrop = true
        imageOptions.skipMemoryCache = true

        // When
        val result = imageOptions.toRequestOptions()

        // Then
        result.onlyRetrieveFromCache.shouldBeTrue()
        result.isSkipMemoryCacheSet.shouldBeTrue()
        result.overrideWidth shouldBe 100
        result.overrideHeight shouldBe 200
        result.isTransformationSet.shouldBeTrue()
    }
}