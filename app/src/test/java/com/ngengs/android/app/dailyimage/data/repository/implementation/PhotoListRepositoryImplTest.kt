package com.ngengs.android.app.dailyimage.data.repository.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.ext.toPhotosLocal
import com.ngengs.android.app.dailyimage.data.remote.model.Pagination
import com.ngengs.android.app.dailyimage.data.remote.model.PaginationData
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.source.FakePhotoLocalDataSource
import com.ngengs.android.app.dailyimage.helpers.fake.data.source.FakePhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
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
class PhotoListRepositoryImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakePhotoRemoteDataSource = FakePhotoRemoteDataSource()
    private val fakePhotoLocalDataSource = FakePhotoLocalDataSource()
    private lateinit var repository: PhotoListRepositoryImpl

    @Before
    fun setUp() {
        repository = PhotoListRepositoryImpl(
            fakePhotoLocalDataSource,
            fakePhotoRemoteDataSource,
            dispatcherProvider
        )
    }

    @After
    fun tearDown() {
        fakePhotoLocalDataSource.reset()
        fakePhotoRemoteDataSource.reset()
    }

    @Test
    fun get_latest_nextPage_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<Photos>(forge) }
        val page = forge.aLong(min = 10, max = 100)
        val lastPage = forge.aLong(min = page, max = 100)
        fakePhotoRemoteDataSource.photoList = PaginationData(
            pagination = Pagination(last = lastPage),
            data = data
        )

        // When
        val result = repository.get(page, ApiConstant.ORDER_BY_LATEST)

        // Then
        result.isComplete shouldBe (page == lastPage)
        result.data shouldBe data.map { it.toPhotosLocal() }
        fakePhotoLocalDataSource.latestPhotos.shouldBeEmpty()
    }

    @Test
    fun get_latest_firstPage_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<Photos>(forge) }
        val page = 1L
        val lastPage = 1L
        fakePhotoLocalDataSource.saveLatest(
            (1..5).map { DataForger.forgeParcelStableId(forge) }
        )
        fakePhotoRemoteDataSource.photoList = PaginationData(
            pagination = Pagination(last = lastPage),
            data = data
        )

        // When
        val result = repository.get(page, ApiConstant.ORDER_BY_LATEST)

        // Then
        result.isComplete.shouldBeTrue()
        result.data shouldBe data.map { it.toPhotosLocal() }
        fakePhotoLocalDataSource.getLatest() shouldBe data.map { it.toPhotosLocal() }
    }

    @Test
    fun get_popular_nextPage_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<Photos>(forge) }
        val page = forge.aLong(min = 10, max = 100)
        val lastPage = forge.aLong(min = page, max = 100)
        fakePhotoRemoteDataSource.photoList = PaginationData(
            pagination = Pagination(last = lastPage),
            data = data
        )

        // When
        val result = repository.get(page, ApiConstant.ORDER_BY_POPULAR)

        // Then
        result.isComplete shouldBe (page == lastPage)
        result.data shouldBe data.map { it.toPhotosLocal() }
        fakePhotoLocalDataSource.popularPhotos.shouldBeEmpty()
    }

    @Test
    fun get_popular_firstPage_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcelStableId<Photos>(forge) }
        val page = 1L
        val lastPage = 1L
        fakePhotoLocalDataSource.savePopular(
            (1..5).map { DataForger.forgeParcelStableId(forge) }
        )
        fakePhotoRemoteDataSource.photoList = PaginationData(
            pagination = Pagination(last = lastPage),
            data = data
        )

        // When
        val result = repository.get(page, ApiConstant.ORDER_BY_POPULAR)

        // Then
        result.isComplete.shouldBeTrue()
        result.data shouldBe data.map { it.toPhotosLocal() }
        fakePhotoLocalDataSource.getPopular() shouldBe data.map { it.toPhotosLocal() }
    }

    @Test(expected = Exception::class)
    fun get_failed() = runTest {
        // Given
        val page = forge.aLong(min = 1, max = 100)
        fakePhotoRemoteDataSource.photoList = null

        // When
        repository.get(page, ApiConstant.ORDER_BY_POPULAR)
    }

    @Test
    fun cache_latest_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) }
        fakePhotoLocalDataSource.saveLatest(data)

        // When
        val result = repository.cache(ApiConstant.ORDER_BY_LATEST)

        // Then
        result shouldBe data
    }

    @Test
    fun cache_popular_success() = runTest {
        // Given
        val data = (1..20).map { DataForger.forgeParcel<PhotosLocal>(forge) }
        fakePhotoLocalDataSource.savePopular(data)

        // When
        val result = repository.cache(ApiConstant.ORDER_BY_POPULAR)

        // Then
        result shouldBe data
    }

    @Test
    fun cache_failed() = runTest {
        // Given
        fakePhotoLocalDataSource.clearLatest()

        // When
        val result = repository.cache(ApiConstant.ORDER_BY_LATEST)

        // Then
        result.shouldBeEmpty()
    }
}