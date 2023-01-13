package com.ngengs.android.app.dailyimage.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import kotlinx.coroutines.flow.Flow

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface GetSearchedPhotoUseCase {
    suspend operator fun invoke(
        text: String,
        page: Long,
        oldData: CompletableCachedData<PhotosLocal>?
    ): Flow<Results<CompletableCachedData<PhotosLocal>>>
}