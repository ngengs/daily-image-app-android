package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.CLIENT
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.EmptyStackException
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
class GetFavoriteListUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository,
    private val dispatcher: DispatcherProvider,
) : GetFavoriteListUseCase {
    override fun invoke(): Flow<Results<CompletableData<PhotosLocal>>> = repository.get()
        .map {
            Timber.d("map, thread: ${Thread.currentThread().name}")
            if (it.isEmpty()) Results.Failure(EmptyStackException(), EMPTY)
            else Results.Success(CompletableData(isComplete = true, data = it))
        }
        .catch {
            Timber.d("catch, thread: ${Thread.currentThread().name}")
            Results.Failure<CompletableData<PhotosLocal>>(it, CLIENT)
        }
        .flowOn(dispatcher.io())
}