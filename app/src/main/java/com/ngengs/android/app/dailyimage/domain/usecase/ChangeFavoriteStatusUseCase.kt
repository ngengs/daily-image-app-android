package com.ngengs.android.app.dailyimage.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
interface ChangeFavoriteStatusUseCase {
    suspend operator fun invoke(data: PhotosLocal, currentStatus: Boolean)
}