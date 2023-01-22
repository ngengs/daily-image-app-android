package com.ngengs.android.app.dailyimage.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.domain.model.Results
import kotlinx.coroutines.flow.Flow

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
interface GetFavoriteListUseCase {
    operator fun invoke(): Flow<Results<CompletableData<PhotosLocal>>>
}