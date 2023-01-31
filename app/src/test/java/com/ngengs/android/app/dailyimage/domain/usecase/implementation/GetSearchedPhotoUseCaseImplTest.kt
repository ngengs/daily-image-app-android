package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import app.cash.turbine.test
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results.Failure
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.NETWORK
import com.ngengs.android.app.dailyimage.domain.model.Results.Loading
import com.ngengs.android.app.dailyimage.domain.model.Results.Success
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakeSearchRepository
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeFalse
import com.ngengs.android.libs.test.utils.ext.shouldBeNull
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
class GetSearchedPhotoUseCaseImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakeSearchRepository()
    private lateinit var useCase: GetSearchedPhotoUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetSearchedPhotoUseCaseImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun test_useCase_firstPage_success() = runTest {
        // Given
        val oldData = DataForger.forgeParcelStableId<PhotosLocal>(forge, 5)
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 20)
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, 1L, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Loading::class
            val firstItemData = firstItem as Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Success::class
            val secondItemData = secondItem as Success
            secondItemData.data.isCache.shouldBeFalse()
            secondItemData.data.isComplete shouldBe completableData.isComplete
            secondItemData.data.data shouldBe completableData.data

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPage_success() = runTest {
        // Given
        val oldData = DataForger.forgeParcelStableId<PhotosLocal>(forge, 5)
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 20)
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val page = forge.aLong(min = 2, max = 20)
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, page, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Loading::class
            val firstItemData = firstItem as Loading
            firstItemData.oldData.shouldNotNull()
            firstItemData.oldData?.isComplete shouldBe completableOldData.isComplete
            firstItemData.oldData?.data shouldBe completableOldData.data

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Success::class
            val secondItemResult = secondItem as Success
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
        val oldData = DataForger.forgeParcelStableId<PhotosLocal>(forge, 5)
        val data = DataForger.forgeParcelStableId<PhotosLocal>(forge, 20)
        val completableOldData = CompletableCachedData(isComplete = false, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val page = forge.aLong(min = 2, max = 20)
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, page, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Loading::class
            val firstItemData = firstItem as Loading
            firstItemData.oldData.shouldNotNull()
            firstItemData.oldData?.isComplete shouldBe completableOldData.isComplete
            firstItemData.oldData?.data shouldBe completableOldData.data

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Success::class
            val secondItemResult = secondItem as Success
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
        fakeRepository.searchList = completableData
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, 2L, null).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Loading::class
            val firstItemData = firstItem as Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Failure::class
            val secondItemResult = secondItem as Failure
            secondItemResult.type shouldBe EMPTY

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_failure() = runTest {
        // Given
        fakeRepository.searchList = null
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, 1L, null).test {
            // Then
            val firstItem = awaitItem()
            firstItem shouldInstanceOf Loading::class
            val firstItemData = firstItem as Loading
            firstItemData.oldData.shouldBeNull()

            val secondItem = awaitItem()
            secondItem shouldInstanceOf Failure::class
            val secondItemResult = secondItem as Failure
            secondItemResult.type shouldBe NETWORK

            cancelAndIgnoreRemainingEvents()
        }
    }
}