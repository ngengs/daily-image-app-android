package com.ngengs.android.app.dailyimage.data.repository.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.toPhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class SearchRepositoryImpl(
    private val remoteDataSource: PhotoRemoteDataSource,
    private val dispatcher: DispatcherProvider
) : SearchRepository {
    override suspend fun search(text: String, page: Long) = withContext(dispatcher.io()) {
        Timber.d("search, thread: ${Thread.currentThread().name}")
        val remoteData = remoteDataSource.search(text, page)
        CompletableData(
            isComplete = page == remoteData.pagination.last,
            data = remoteData.data.map { it.toPhotosLocal() }
        )
    }

    override suspend fun searchSuggestion(text: String) = withContext(dispatcher.io()) {
        Timber.d("searchSuggestion, thread: ${Thread.currentThread().name}")
        remoteDataSource.searchSuggestion(text)
    }
}