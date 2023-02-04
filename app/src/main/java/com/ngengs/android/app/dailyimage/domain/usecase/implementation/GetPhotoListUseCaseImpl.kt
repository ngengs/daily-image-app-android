package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType
import com.ngengs.android.app.dailyimage.domain.usecase.GetPhotoListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class GetPhotoListUseCaseImpl @Inject constructor(
    private val repository: PhotoListRepository,
    private val dispatcher: DispatcherProvider,
) : GetPhotoListUseCase {
    override suspend fun invoke(
        page: Long,
        orderBy: String,
        oldData: CompletableCachedData<PhotosLocal>?,
    ): Flow<Results<CompletableCachedData<PhotosLocal>>> = flow {
        Timber.d("use-case invoke, thread: ${Thread.currentThread().name}")
        val (oldDataList, oldDataIsComplete) = if (page > 1L) {
            oldData?.data to (oldData?.isComplete == true)
        } else {
            null to false
        }
        val oldDataToUse = if (oldDataList != null) {
            CompletableCachedData(oldDataIsComplete, oldDataList)
        } else {
            null
        }

        emit(Results.Loading(oldDataToUse))
        try {
            if (page == 1L) {
                val cache = repository.cache(orderBy)
                if (cache.isNotEmpty()) {
                    val cacheData =
                        CompletableCachedData(isComplete = true, data = cache, isCache = true)
                    emit(Results.Success(cacheData))
                }
            }

            val result = repository.get(page, orderBy)
            val data = result.data

            val combineData = if (page > 1L) {
                oldDataList.orEmpty() + data
            } else {
                data
            }
            if (combineData.isEmpty()) {
                emit(Results.Failure(Exception(), FailureType.EMPTY, oldDataToUse))
            } else {
                emit(Results.Success(CompletableCachedData(result.isComplete, combineData)))
            }
        } catch (e: Exception) {
            Timber.e(e)
            emit(Results.Failure(e, FailureType.NETWORK, oldDataToUse))
        }
    }.flowOn(dispatcher.io())
}