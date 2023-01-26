package com.ngengs.android.app.dailyimage.data.source.implementation

import androidx.core.net.toUri
import com.ngengs.android.app.dailyimage.data.remote.UnsplashAPI
import com.ngengs.android.app.dailyimage.data.remote.UnsplashPublicAPI
import com.ngengs.android.app.dailyimage.data.remote.model.Pagination
import com.ngengs.android.app.dailyimage.data.remote.model.PaginationData
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.HEADER_LINK
import com.ngengs.android.app.dailyimage.utils.common.ext.debugTry
import com.ngengs.android.app.dailyimage.utils.common.ext.debugTrySuspend
import kotlinx.coroutines.withContext
import okhttp3.Headers
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class PhotoRemoteDataSourceImpl @Inject constructor(
    private val api: UnsplashAPI,
    private val apiPublic: UnsplashPublicAPI,
    private val dispatcher: DispatcherProvider
) : PhotoRemoteDataSource {

    override suspend fun getPhotoList(page: Long, orderBy: String) = withContext(dispatcher.io()) {
        Timber.d("getPhotoList, thread: ${Thread.currentThread().name}")
        val data = api.photos(page, orderBy)
        if (!data.isSuccessful) throw HttpException(data)
        val body = data.body() ?: emptyList()
        val header = data.headers()
        val pagination = extractPaginationFromHeader(header)
        PaginationData(pagination, body)
    }

    override suspend fun search(text: String, page: Long) = withContext(dispatcher.io()) {
        Timber.d("search, thread: ${Thread.currentThread().name}")
        val data = api.search(page, text)
        val pagination = Pagination(
            prev = page - 1,
            next = page + 1,
            last = data.totalPages
        )
        PaginationData(pagination, data.results)
    }

    override suspend fun searchSuggestion(text: String) = withContext(dispatcher.io()) {
        Timber.d("searchSuggestion, thread: ${Thread.currentThread().name}")
        val data = debugTrySuspend { apiPublic.autocomplete(text) }
        data?.autocomplete?.map { it.query }.orEmpty()
    }

    private fun extractPaginationFromHeader(header: Headers): Pagination {
        Timber.d("extractPaginationFromHeader, thread: ${Thread.currentThread().name}")
        val linksHeader = header.values(HEADER_LINK)
        if (linksHeader.isEmpty()) return Pagination()
        val linkString = linksHeader.last()
        var first = 1L
        var prev = 1L
        var next = 1L
        var last = 1L

        val splitLink = linkString.split(",")
        splitLink.forEach { link ->
            val cleanLink = link.trim()
            val linkData = cleanLink.split(";")
            val linkUri = linkData.first().trim().replace("<", "").replace(">", "")
            val linkType = linkData.last().trim().replace("rel=", "").replace("\"", "")
            debugTry {
                val uri = linkUri.toUri()
                Timber.d("extractPaginationFromHeader: $uri, ${uri.query}, $linkType")
                val page = uri.getQueryParameter("page")?.toLong() ?: 1L
                when (linkType) {
                    "first" -> first = page
                    "prev" -> prev = page
                    "next" -> next = page
                    "last" -> last = page
                }
            }
        }

        return Pagination(first, prev, next, last)
    }
}