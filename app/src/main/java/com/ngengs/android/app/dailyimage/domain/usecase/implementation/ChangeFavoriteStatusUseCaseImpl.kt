package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.usecase.ChangeFavoriteStatusUseCase
import kotlinx.coroutines.withContext

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
class ChangeFavoriteStatusUseCaseImpl(
    private val repository: FavoriteRepository,
    private val dispatcher: DispatcherProvider,
) : ChangeFavoriteStatusUseCase {
    override suspend fun invoke(
        data: PhotosLocal,
        currentStatus: Boolean
    ) = withContext(dispatcher.io()) {
        if (currentStatus) repository.removeFavorite(data)
        else repository.setFavorite(data)
    }
}