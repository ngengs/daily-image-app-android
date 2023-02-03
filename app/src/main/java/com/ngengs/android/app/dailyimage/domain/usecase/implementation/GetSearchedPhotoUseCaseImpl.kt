package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class GetSearchedPhotoUseCaseImpl @Inject constructor(
    private val repository: SearchRepository,
    private val dispatcher: DispatcherProvider,
) : GetSearchedPhotoUseCase {
    override suspend fun invoke(
        text: String,
        page: Long,
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
            val result = repository.search(text, page)
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