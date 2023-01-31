package com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteStatusUseCase

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
class FakeGetFavoriteStatusUseCase : GetFavoriteStatusUseCase {
    var status: Boolean = false

    override suspend fun invoke(data: PhotosLocal): Boolean = status

    fun reset() {
        status = false
    }
}