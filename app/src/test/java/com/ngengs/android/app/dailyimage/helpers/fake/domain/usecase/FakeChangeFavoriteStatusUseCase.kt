package com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.usecase.ChangeFavoriteStatusUseCase

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
class FakeChangeFavoriteStatusUseCase : ChangeFavoriteStatusUseCase {
    var status: Boolean = false

    override suspend fun invoke(data: PhotosLocal, currentStatus: Boolean) {
        status = !currentStatus
    }

    fun reset() {
        status = false
    }
}