package com.ngengs.android.app.dailyimage.data.repository.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.toPhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class PhotoListRepositoryImpl(
    private val localDataSource: PhotoLocalDataSource,
    private val remoteDataSource: PhotoRemoteDataSource,
    private val dispatcher: DispatcherProvider,
) : PhotoListRepository {
    override suspend fun get(
        page: Long,
        orderBy: String
    ): CompletableData<PhotosLocal> = withContext(dispatcher.io()) {
        Timber.d("get, thread: ${Thread.currentThread().name}")
        val remoteData = remoteDataSource.getPhotoList(page, orderBy)
        val photosSimple = remoteData.data.map { it.toPhotosLocal() }

        if (page == 1L) {
            if (orderBy == ApiConstant.ORDER_BY_LATEST) {
                localDataSource.clearLatest()
                localDataSource.saveLatest(photosSimple)
            } else {
                localDataSource.clearPopular()
                localDataSource.savePopular(photosSimple)
            }
        }
        val isComplete = page == remoteData.pagination.last
        Timber.d("get: ${remoteData.pagination}")

        CompletableData(isComplete, photosSimple)
    }

    override suspend fun cache(orderBy: String): List<PhotosLocal> = withContext(dispatcher.io()) {
        Timber.d("cache, thread: ${Thread.currentThread().name}")
        if (orderBy == ApiConstant.ORDER_BY_LATEST) {
            localDataSource.getLatest()
        } else {
            localDataSource.getPopular()
        }
    }
}