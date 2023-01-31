package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakePhotoListRepository
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
import com.ngengs.android.libs.test.utils.ext.shouldBeNull
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
import com.ngengs.android.libs.test.utils.ext.shouldHasSize
import com.ngengs.android.libs.test.utils.ext.shouldInstanceOf
import com.ngengs.android.libs.test.utils.ext.shouldNotNull
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPhotoListUseCaseImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakePhotoListRepository()
    private lateinit var useCase: GetPhotoListUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetPhotoListUseCaseImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun test_useCase_firstPage_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val data = (1..20).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val cache = (1..10).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache

        // When
        useCase.invoke(1L, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Loading::class
            val firstItemData = firstItem as Results.Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Success::class
            val secondItemResult = secondItem as Results.Success
            secondItemResult.data.isCache.shouldBeTrue()
            secondItemResult.data.isComplete.shouldBeTrue()
            secondItemResult.data.data shouldBe cache

            val thirdItem = awaitItem()
            thirdItem shouldInstanceOf Results.Success::class
            val thirdItemData = thirdItem as Results.Success
            thirdItemData.data.isCache.shouldBeFalse()
            thirdItemData.data.isComplete shouldBe completableData.isComplete
            thirdItemData.data.data shouldBe completableData.data

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPage_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val data = (1..20).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val cache = (1..10).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache
        val page = forge.aLong(min = 2, max = 20)

        // When
        useCase.invoke(page, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Loading::class
            val firstItemData = firstItem as Results.Loading
            firstItemData.oldData.shouldNotNull()
            firstItemData.oldData?.isComplete shouldBe completableOldData.isComplete
            firstItemData.oldData?.data shouldBe completableOldData.data

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Success::class
            val secondItemResult = secondItem as Results.Success
            secondItemResult.data.isCache.shouldBeFalse()
            secondItemResult.data.isCache shouldBe completableData.isComplete
            secondItemResult.data.data shouldHasSize (oldData.size + data.size)
            secondItemResult.data.data shouldBe (oldData + data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPageAgain_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val data = (1..20).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val cache = (1..10).map { DataForger.forgeParcelStableId<PhotosLocal>(forge) }
        val completableOldData = CompletableCachedData(isComplete = false, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache
        val page = forge.aLong(min = 10, max = 20)

        // When
        useCase.invoke(page, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Loading::class
            val firstItemData = firstItem as Results.Loading
            firstItemData.oldData.shouldNotNull()
            firstItemData.oldData?.isComplete shouldBe completableOldData.isComplete
            firstItemData.oldData?.data shouldBe completableOldData.data

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Success::class
            val secondItemResult = secondItem as Results.Success
            secondItemResult.data.isCache.shouldBeFalse()
            secondItemResult.data.isCache shouldBe completableData.isComplete
            secondItemResult.data.data shouldHasSize (oldData.size + data.size)
            secondItemResult.data.data shouldBe (oldData + data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_empty() = runTest {
        // Given
        val completableData = CompletableData(isComplete = false, data = listOf<PhotosLocal>())
        fakeRepository.photoList = completableData

        // When
        useCase.invoke(2L, ApiConstant.ORDER_BY_LATEST, null).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Loading::class
            val firstItemData = firstItem as Results.Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Failure::class
            val secondItemResult = secondItem as Results.Failure
            secondItemResult.type shouldBe FailureType.EMPTY

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_failure() = runTest {
        // Given
        fakeRepository.photoList = null
        fakeRepository.cacheList = emptyList()

        // When
        useCase.invoke(1L, ApiConstant.ORDER_BY_LATEST, null).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Results.Loading::class
            val firstItemData = firstItem as Results.Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Results.Failure::class
            val secondItemResult = secondItem as Results.Failure
            secondItemResult.type shouldBe FailureType.NETWORK

            cancelAndIgnoreRemainingEvents()
        }
    }
}