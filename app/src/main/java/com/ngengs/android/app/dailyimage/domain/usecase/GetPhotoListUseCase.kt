package com.ngengs.android.app.dailyimage.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.OrderBy
import kotlinx.coroutines.flow.Flow

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface GetPhotoListUseCase {
    suspend operator fun invoke(
        page: Long,
        @OrderBy orderBy: String,
        oldData: CompletableCachedData<PhotosLocal>?
    ): Flow<Results<CompletableCachedData<PhotosLocal>>>
}