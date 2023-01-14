package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakePhotoListRepository
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
import com.ngengs.android.libs.test.utils.DataForger
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
        val oldData = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val cache = (1..10).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache

        // When
        useCase.invoke(1L, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Results.Loading::class.java)
            val firstItemData = firstItem as Results.Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Results.Success::class.java)
            val secondItemResult = secondItem as Results.Success
            assertThat(secondItemResult.data.isCache).isTrue()
            assertThat(secondItemResult.data.isComplete).isTrue()
            assertThat(secondItemResult.data.data).isEqualTo(cache)

            val thirdItem = awaitItem()
            assertThat(thirdItem).isInstanceOf(Results.Success::class.java)
            val thirdItemData = thirdItem as Results.Success
            assertThat(thirdItemData.data.isCache).isFalse()
            assertThat(thirdItemData.data.isComplete).isEqualTo(completableData.isComplete)
            assertThat(thirdItemData.data.data).isEqualTo(completableData.data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPage_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val cache = (1..10).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache
        val page = forge.aLong(min = 2, max = 20)

        // When
        useCase.invoke(page, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Results.Loading::class.java)
            val firstItemData = firstItem as Results.Loading
            assertThat(firstItemData.oldData).isNotNull()
            assertThat(firstItemData.oldData?.isComplete).isEqualTo(completableOldData.isComplete)
            assertThat(firstItemData.oldData?.data).isEqualTo(completableOldData.data)

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Results.Success::class.java)
            val secondItemResult = secondItem as Results.Success
            assertThat(secondItemResult.data.isCache).isFalse()
            assertThat(secondItemResult.data.isCache).isEqualTo(completableData.isComplete)
            assertThat(secondItemResult.data.data).hasSize(oldData.size + data.size)
            assertThat(secondItemResult.data.data).isEqualTo(oldData + data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPageAgain_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val cache = (1..10).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val completableOldData = CompletableCachedData(isComplete = false, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.photoList = completableData
        fakeRepository.cacheList = cache
        val page = forge.aLong(min = 10, max = 20)

        // When
        useCase.invoke(page, ApiConstant.ORDER_BY_LATEST, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Results.Loading::class.java)
            val firstItemData = firstItem as Results.Loading
            assertThat(firstItemData.oldData).isNotNull()
            assertThat(firstItemData.oldData?.isComplete).isEqualTo(completableOldData.isComplete)
            assertThat(firstItemData.oldData?.data).isEqualTo(completableOldData.data)

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Results.Success::class.java)
            val secondItemResult = secondItem as Results.Success
            assertThat(secondItemResult.data.isCache).isFalse()
            assertThat(secondItemResult.data.isCache).isEqualTo(completableData.isComplete)
            assertThat(secondItemResult.data.data).hasSize(oldData.size + data.size)
            assertThat(secondItemResult.data.data).isEqualTo(oldData + data)

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
            assertThat(firstItem).isInstanceOf(Results.Loading::class.java)
            val firstItemData = firstItem as Results.Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Results.Failure::class.java)
            val secondItemResult = secondItem as Results.Failure
            assertThat(secondItemResult.type).isEqualTo(FailureType.EMPTY)

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
            assertThat(firstItem).isInstanceOf(Results.Loading::class.java)
            val firstItemData = firstItem as Results.Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Results.Failure::class.java)
            val secondItemResult = secondItem as Results.Failure
            assertThat(secondItemResult.type).isEqualTo(FailureType.NETWORK)

            cancelAndIgnoreRemainingEvents()
        }
    }
}