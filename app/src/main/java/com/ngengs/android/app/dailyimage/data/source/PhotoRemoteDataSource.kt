package com.ngengs.android.app.dailyimage.data.source

import com.ngengs.android.app.dailyimage.data.remote.model.PaginationData
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.OrderBy

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface PhotoRemoteDataSource {
    suspend fun getPhotoList(page: Long, @OrderBy orderBy: String): PaginationData<Photos>
}