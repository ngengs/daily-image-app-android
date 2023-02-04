package com.ngengs.android.app.dailyimage.data.repository.implementation

import com.ngengs.android.app.dailyimage.data.model.ext.toPhotosLocal
import com.ngengs.android.app.dailyimage.data.remote.model.Pagination
import com.ngengs.android.app.dailyimage.data.remote.model.PaginationData
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.source.FakePhotoRemoteDataSource
import com.ngengs.android.libs.test.utils.DataForger
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
import com.ngengs.android.libs.test.utils.ext.shouldBeTrue
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakePhotoRemoteDataSource = FakePhotoRemoteDataSource()
    private lateinit var repository: SearchRepositoryImpl

    @Before
    fun setUp() {
        repository = SearchRepositoryImpl(fakePhotoRemoteDataSource, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakePhotoRemoteDataSource.reset()
    }

    @Test
    fun search_nextPage_success() = runTest {
        // Given
        val data = DataForger.forgeParcelStableId<Photos>(forge, 20)
        val page = forge.aLong(min = 10, max = 100)
        val last = forge.aLong(min = page, max = 100)
        fakePhotoRemoteDataSource.searchList = PaginationData(
            pagination = Pagination(last = last),
            data = data,
        )

        // When
        val result = repository.search(forge.anAlphabeticalString(size = 10), page)

        // Then
        result.isComplete shouldBe (page == last)
        result.data shouldBe data.map { it.toPhotosLocal() }
    }

    @Test
    fun search_firstPage_success() = runTest {
        // Given
        val data = DataForger.forgeParcelStableId<Photos>(forge, 20)
        val page = 1L
        val last = 1L
        fakePhotoRemoteDataSource.searchList = PaginationData(
            pagination = Pagination(last = last),
            data = data,
        )

        // When
        val result = repository.search(forge.anAlphabeticalString(size = 10), page)

        // Then
        result.isComplete.shouldBeTrue()
        result.data shouldBe data.map { it.toPhotosLocal() }
    }

    @Test(expected = Exception::class)
    fun search_failed() = runTest {
        // Given
        val page = forge.aLong(min = 1, max = 100)
        fakePhotoRemoteDataSource.searchList = null

        // When
        repository.search(forge.anAlphabeticalString(size = 10), page)
    }

    @Test
    fun searchSuggestion_success() = runTest {
        // Given
        val data = (1..5).map { forge.anAlphabeticalString(size = 15) }
        fakePhotoRemoteDataSource.suggestion = data

        // When
        val result = repository.searchSuggestion(forge.anAlphabeticalString(size = 5))

        // Then
        result shouldBe data
    }

    @Test
    fun searchSuggestion_failed() = runTest {
        // When
        val result = repository.searchSuggestion(forge.anAlphabeticalString(size = 5))

        // Then
        result.shouldBeEmpty()
    }
}