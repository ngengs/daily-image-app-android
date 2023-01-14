package com.ngengs.android.app.dailyimage.data.source.implementation

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ngengs.android.app.dailyimage.data.remote.UnsplashAPI
import com.ngengs.android.app.dailyimage.data.remote.UnsplashPublicAPI
import com.ngengs.android.app.dailyimage.data.remote.model.AutoComplete
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.data.remote.model.SearchResult
import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
import com.ngengs.android.app.dailyimage.utils.network.MoshiConfig
import com.ngengs.android.libs.test.utils.ResourceFile
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import com.squareup.moshi.Types
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.HttpException
import java.net.HttpURLConnection

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [32])
class PhotoRemoteDataSourceImplTest {

    @get:Rule
    val forge = ForgeRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val mockWebServer = MockWebServer()
    private var isSuccessResponse: Boolean = false
    private var isContainLinkHeader: Boolean = true
    private var autoCompleteSuggestionText: String = ""

    private lateinit var api: UnsplashAPI
    private lateinit var apiPublic: UnsplashPublicAPI
    private lateinit var dataSource: PhotoRemoteDataSourceImpl

    private val photoListJson by lazy {
        ResourceFile.getJson("unsplash/unsplash-photo-list.json")
    }
    private val photoSearchJson by lazy {
        ResourceFile.getJson("unsplash/unsplash-photo-search.json")
    }
    private val autoCompleteJson by lazy {
        ResourceFile.getJson("unsplash-public/unsplash-autocomplete.json")
    }
    private val serverDispatcher by lazy {
        object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (!isSuccessResponse) {
                    return MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
                return when (request.requestUrl?.pathSegments?.joinToString("/").orEmpty()) {
                    "photos" -> {
                        val mockResponse = MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(photoListJson)
                        if (isContainLinkHeader) {
                            val headerLinkData =
                                "<https://api.unsplash.com/photos?page=1>; rel=\"first\", " +
                                    "<https://api.unsplash.com/photos?page=2>; rel=\"prev\", " +
                                    "<https://api.unsplash.com/photos?page=346>; rel=\"last\", " +
                                    "<https://api.unsplash.com/photos?page=4>; rel=\"next\""
                            mockResponse.setHeader("Link", headerLinkData)
                        }
                        mockResponse
                    }
                    "search/photos" -> {
                        val mockResponse = MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(photoSearchJson)
                        if (isContainLinkHeader) {
                            val headerLinkData =
                                "<https://api.unsplash.com/photos?page=1>; rel=\"first\", " +
                                    "<https://api.unsplash.com/photos?page=2>; rel=\"prev\", " +
                                    "<https://api.unsplash.com/photos?page=346>; rel=\"last\", " +
                                    "<https://api.unsplash.com/photos?page=4>; rel=\"next\""
                            mockResponse.setHeader("Link", headerLinkData)
                        }
                        mockResponse
                    }
                    "nautocomplete/$autoCompleteSuggestionText" -> {
                        MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(autoCompleteJson)
                    }
                    else ->
                        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }
    }

    @Before
    fun setUp() {
        isSuccessResponse = true
        isContainLinkHeader = true
        autoCompleteSuggestionText = ""
        mockWebServer.dispatcher = serverDispatcher
        mockWebServer.start()
        api = UnsplashAPI.instantiate(
            ApplicationProvider.getApplicationContext(),
            mockWebServer.url("/").toString()
        )
        apiPublic = UnsplashPublicAPI.instantiate(
            ApplicationProvider.getApplicationContext(),
            mockWebServer.url("/").toString()
        )
        dataSource = PhotoRemoteDataSourceImpl(api, apiPublic, dispatcherProvider)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getPhotoList_success() = runTest {
        // Given
        val page = forge.aLong(min = 1, max = 100)
        val orderBy = ApiConstant.ORDER_BY_LATEST
        val moshiAdapterType =
            Types.newParameterizedType(List::class.java, Photos::class.java)
        val dataFromJson =
            MoshiConfig.moshi.adapter<List<Photos>>(moshiAdapterType).fromJson(photoListJson)

        // When
        val result = dataSource.getPhotoList(page, orderBy)

        // Then
        assertThat(result.pagination.last).isEqualTo(346) // From headerLinkData
        assertThat(result.pagination.next).isEqualTo(4) // From headerLinkData
        assertThat(result.pagination.prev).isEqualTo(2) // From headerLinkData
        assertThat(result.pagination.first).isEqualTo(1) // From headerLinkData
        assertThat(result.data).isEqualTo(dataFromJson)
    }

    @Test
    fun getPhotoList_success_withoutLinkData() = runTest {
        // Given
        isContainLinkHeader = false
        val page = forge.aLong(min = 1, max = 100)
        val orderBy = ApiConstant.ORDER_BY_POPULAR
        val moshiAdapterType =
            Types.newParameterizedType(List::class.java, Photos::class.java)
        val dataFromJson =
            MoshiConfig.moshi.adapter<List<Photos>>(moshiAdapterType).fromJson(photoListJson)

        // When
        val result = dataSource.getPhotoList(page, orderBy)

        // Then
        assertThat(result.pagination.last).isEqualTo(1) // Default
        assertThat(result.pagination.next).isEqualTo(1) // Default
        assertThat(result.pagination.prev).isEqualTo(1) // Default
        assertThat(result.pagination.first).isEqualTo(1) // Default
        assertThat(result.data).isEqualTo(dataFromJson)
    }

    @Test(expected = HttpException::class)
    fun getPhotoList_failed() = runTest {
        // Given
        isSuccessResponse = false
        val page = forge.aLong(min = 1, max = 100)
        val orderBy = ApiConstant.ORDER_BY_POPULAR

        // When
        dataSource.getPhotoList(page, orderBy)
    }

    @Test
    fun search_success() = runTest {
        // Given
        val queryText = forge.anAlphaNumericalString(size = 10)
        val page = forge.aLong(min = 1, max = 100)
        val dataFromJson =
            MoshiConfig.moshi.adapter(SearchResult::class.java).fromJson(photoSearchJson)

        // When
        val result = dataSource.search(queryText, page)
        val apiResult = api.search(page, queryText)

        // Then
        assertThat(result.pagination.last).isEqualTo(dataFromJson?.totalPages)
        assertThat(result.pagination.next).isEqualTo(page + 1)
        assertThat(result.pagination.prev).isEqualTo(page - 1)
        assertThat(result.pagination.first).isEqualTo(1)
        assertThat(result.data).isEqualTo(dataFromJson?.results)
        assertThat(result.data).isEqualTo(apiResult.results)
        assertThat(apiResult.totalPages).isEqualTo(dataFromJson?.totalPages)
        assertThat(apiResult.total).isEqualTo(dataFromJson?.total)
    }

    @Test(expected = HttpException::class)
    fun search_failed() = runTest {
        // Given
        isSuccessResponse = false
        val queryText = forge.anAlphaNumericalString(size = 10)
        val page = forge.aLong(min = 1, max = 100)

        // When
        dataSource.search(queryText, page)
    }

    @Test
    fun searchSuggestion_success() = runTest {
        // Given
        autoCompleteSuggestionText = forge.anAlphabeticalString(size = 10)
        val dataFromJson =
            MoshiConfig.moshi.adapter(AutoComplete::class.java).fromJson(autoCompleteJson)

        // When
        val result = dataSource.searchSuggestion(autoCompleteSuggestionText)
        val apiResult = apiPublic.autocomplete(autoCompleteSuggestionText)

        // Then
        assertThat(result).isEqualTo(dataFromJson?.autocomplete?.map { it.query })
        assertThat(result).isEqualTo(apiResult.autocomplete.map { it.query })
        assertThat(apiResult.fuzzy).isNotEmpty()
        assertThat(apiResult.didYouMean).isNotEmpty()
    }

    @Test
    fun searchSuggestion_failed() = runTest {
        // Given
        isSuccessResponse = false
        autoCompleteSuggestionText = forge.anAlphabeticalString(size = 10)

        // When
        val result = dataSource.searchSuggestion(autoCompleteSuggestionText)

        // Then
        assertThat(result).isEmpty()
    }
}