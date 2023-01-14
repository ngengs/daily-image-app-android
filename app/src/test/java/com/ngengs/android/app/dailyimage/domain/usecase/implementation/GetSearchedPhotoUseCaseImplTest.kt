package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
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
        val oldData = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, 1L, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Loading::class.java)
            val firstItemData = firstItem as Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Success::class.java)
            val secondItemData = secondItem as Success
            assertThat(secondItemData.data.isCache).isFalse()
            assertThat(secondItemData.data.isComplete).isEqualTo(completableData.isComplete)
            assertThat(secondItemData.data.data).isEqualTo(completableData.data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_useCase_nextPage_success() = runTest {
        // Given
        val oldData = (1..5).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true } }
        val completableOldData = CompletableCachedData(isComplete = true, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val page = forge.aLong(min = 2, max = 20)
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, page, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Loading::class.java)
            val firstItemData = firstItem as Loading
            assertThat(firstItemData.oldData).isNotNull()
            assertThat(firstItemData.oldData?.isComplete).isEqualTo(completableOldData.isComplete)
            assertThat(firstItemData.oldData?.data).isEqualTo(completableOldData.data)

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Success::class.java)
            val secondItemResult = secondItem as Success
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
        val completableOldData = CompletableCachedData(isComplete = false, data = oldData)
        val completableData = CompletableData(isComplete = false, data = data)
        fakeRepository.searchList = completableData
        val page = forge.aLong(min = 2, max = 20)
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, page, completableOldData).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Loading::class.java)
            val firstItemData = firstItem as Loading
            assertThat(firstItemData.oldData).isNotNull()
            assertThat(firstItemData.oldData?.isComplete).isEqualTo(completableOldData.isComplete)
            assertThat(firstItemData.oldData?.data).isEqualTo(completableOldData.data)

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Success::class.java)
            val secondItemResult = secondItem as Success
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
        fakeRepository.searchList = completableData
        val text = forge.anAlphabeticalString(size = 10)

        // When
        useCase.invoke(text, 2L, null).test {
            // Then
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Loading::class.java)
            val firstItemData = firstItem as Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Failure::class.java)
            val secondItemResult = secondItem as Failure
            assertThat(secondItemResult.type).isEqualTo(EMPTY)

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
            assertThat(firstItem).isInstanceOf(Loading::class.java)
            val firstItemData = firstItem as Loading
            assertThat(firstItemData.oldData).isNull()

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Failure::class.java)
            val secondItemResult = secondItem as Failure
            assertThat(secondItemResult.type).isEqualTo(NETWORK)

            cancelAndIgnoreRemainingEvents()
        }
    }
}