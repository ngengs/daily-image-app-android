package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteStatusUseCase
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
class GetFavoriteStatusUseCaseImpl(
    private val repository: FavoriteRepository,
    private val dispatcher: DispatcherProvider,
) : GetFavoriteStatusUseCase {
    override suspend fun invoke(data: PhotosLocal): Boolean = withContext(dispatcher.io()) {
        Timber.d("invoke, thread: ${Thread.currentThread().name}")
        repository.isFavorite(data)
    }
}